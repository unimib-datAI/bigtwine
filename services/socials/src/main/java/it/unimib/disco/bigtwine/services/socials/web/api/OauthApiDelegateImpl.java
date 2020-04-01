package it.unimib.disco.bigtwine.services.socials.web.api;

import it.unimib.disco.bigtwine.services.socials.config.ApplicationProperties;
import it.unimib.disco.bigtwine.services.socials.domain.RequestToken;
import it.unimib.disco.bigtwine.services.socials.repository.RequestTokenRepository;
import it.unimib.disco.bigtwine.services.socials.connect.AccountConnectionSignUp;
import it.unimib.disco.bigtwine.services.socials.security.SecurityUtils;
import it.unimib.disco.bigtwine.services.socials.security.jwt.JWTFilter;
import it.unimib.disco.bigtwine.services.socials.security.jwt.JWTSocialSignInProvider;
import it.unimib.disco.bigtwine.services.socials.web.api.model.*;
import it.unimib.disco.bigtwine.services.socials.web.rest.errors.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.ApiException;
import org.springframework.social.connect.*;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.oauth1.*;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.NativeWebRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class OauthApiDelegateImpl implements OauthApiDelegate {

    private final NativeWebRequest request;
    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final UsersConnectionRepository usersConnectionRepository;
    private final RequestTokenRepository requestTokenRepository;
    private final AccountConnectionSignUp accountConnectionSignUp;
    private final JWTSocialSignInProvider jwtSocialSignInProvider;
    private final ApplicationProperties applicationProperties;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public OauthApiDelegateImpl(
        NativeWebRequest request,
        ConnectionFactoryLocator connectionLocator,
        UsersConnectionRepository usersConnectionRepository,
        RequestTokenRepository requestTokenRepository,
        AccountConnectionSignUp accountConnectionSignUp,
        JWTSocialSignInProvider jwtSocialSignInProvider,
        ApplicationProperties applicationProperties) {
        this.request = request;
        this.connectionFactoryLocator = connectionLocator;
        this.usersConnectionRepository = usersConnectionRepository;
        this.requestTokenRepository = requestTokenRepository;
        this.accountConnectionSignUp = accountConnectionSignUp;
        this.jwtSocialSignInProvider = jwtSocialSignInProvider;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    private NativeWebRequest getRequestOrThrow() {
        return this
            .getRequest()
            .orElseThrow(() -> new InternalServerErrorException("An unexpected error has occurred"));
    }

    private OAuth1ConnectionFactory getOAuthConnectionFactory(String provider) {
        try {
            ConnectionFactory<?> connectionFactory = this.connectionFactoryLocator.getConnectionFactory(provider);
            return (OAuth1ConnectionFactory)connectionFactory;
        }catch (IllegalArgumentException | ClassCastException e) {
            throw new InvalidProviderException();
        }
    }

    private Connection<?> verify(String provider, String requestToken, String verifier) {
        OAuth1ConnectionFactory oauthConnectionFactory = this.getOAuthConnectionFactory(provider);
        OAuth1Operations oauthOperations = oauthConnectionFactory.getOAuthOperations();

        RequestToken savedRequestToken = this.requestTokenRepository
            .findByTokenValue(requestToken)
            .orElseThrow(OAuthVerificationException::new);

        AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(savedRequestToken.getToken(), verifier);
        OAuthToken accessToken;

        try {
            accessToken = oauthOperations.exchangeForAccessToken(authorizedRequestToken, null);
        }catch(RestClientException e) {
            throw new OAuthVerificationException();
        }

        Connection<?> connection = oauthConnectionFactory.createConnection(accessToken);

        this.requestTokenRepository
            .delete(savedRequestToken);

        return connection;
    }

    private void saveConnection(Connection<?> connection, String userId) {
        ConnectionRepository connectionRepository = this.usersConnectionRepository
            .createConnectionRepository(userId);

        try {
            connectionRepository.addConnection(connection);
        }catch(DuplicateConnectionException e) {
            connectionRepository.updateConnection(connection);
        }
    }

    private Optional<Connection<?>> getValidConnection(String provider, String userId) {
        List<Connection<?>> connections = this.usersConnectionRepository
            .createConnectionRepository(userId)
            .findConnections(provider);

        Connection<?> validConnection = null;
        for (Connection<?> connection: connections) {
            if (connection.hasExpired()) {
                continue;
            }

            if (connection.getApi() instanceof Twitter) {
                try {
                    ((Twitter) connection.getApi()).userOperations().getUserProfile();
                }catch(ApiException e) {
                    continue;
                }
            }

            validConnection = connection;
            break;
        }

        return Optional.ofNullable(validConnection);
    }

    private OAuth10Credentials getOAuthCredentials(String provider, Connection<?> connection) {
        String consumerKey, consumerSecret;
        if ("twitter".equals(provider)) {
            consumerKey = this.applicationProperties.getSecurity().getTwitter().getConsumerKey();
            consumerSecret = this.applicationProperties.getSecurity().getTwitter().getConsumerSecret();
        }else {
            throw new InternalServerErrorException(String.format("Provider not configured: %s", provider));
        }

        ConnectionData connectionData = connection.createData();
        return new OAuth10Credentials()
            .consumerKey(consumerKey)
            .consumerSecret(consumerSecret)
            .accessToken(connectionData.getAccessToken())
            .accessTokenSecret(connectionData.getSecret());
    }

    @Override
    public ResponseEntity<OAuth10AuthorizeResponse> authorizeV1(String provider, OAuth10AuthorizeRequest oauth10AuthorizeRequest) {
        OAuth1ConnectionFactory oauthConnectionFactory = this.getOAuthConnectionFactory(provider);
        OAuth1Operations oauthOperations = oauthConnectionFactory.getOAuthOperations();

        OAuthToken requestToken;
        try {
            requestToken = oauthOperations.fetchRequestToken(oauth10AuthorizeRequest.getCallbackUrl(), null);
        }catch (HttpClientErrorException e) {
            throw new InvalidOAuthCallbackUrlException();
        }catch (RestClientException e) {
            throw new InternalServerErrorException("An error occurred while contacting the service provider. Try again later.");
        }

        this.requestTokenRepository.save(new RequestToken().createdAt(Instant.now()).token(requestToken));

        String authorizeUrl = oauthOperations.buildAuthenticateUrl( requestToken.getValue(), OAuth1Parameters.NONE );
        OAuth10AuthorizeResponse response = new OAuth10AuthorizeResponse()
            .authorizedUrl(authorizeUrl)
            .requestToken(requestToken.getValue());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ConnectResponse> connectV1(String provider, OAuth10VerifyRequest oauth10VerifyRequest) {
        String userId = SecurityUtils
            .getCurrentUserId()
            .orElseThrow(UnauthorizedException::new);

        Connection<?> connection = this.verify(
            provider,
            oauth10VerifyRequest.getRequestToken(),
            oauth10VerifyRequest.getVerifier());

        this.saveConnection(connection, userId);

        ConnectResponse response = new ConnectResponse()
            .user(userId)
            .providerId(provider)
            .providerUserId(connection.createData().getProviderUserId())
            .displayName(connection.getDisplayName());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ConnectedResponse> connectedV1(String provider) {
        String userId = SecurityUtils
            .getCurrentUserId()
            .orElseThrow(UnauthorizedException::new);

        Optional<Connection<?>> connection = this.getValidConnection(provider, userId);

        ConnectedResponse response = new ConnectedResponse()
            .user(userId)
            .connected(connection.isPresent())
            .displayName(connection.map(Connection::getDisplayName).orElse(null));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SignInResponse> signInV1(String provider, SignInRequest signInRequest) {
        Connection<?> connection = this.verify(
            provider,
            signInRequest.getRequestToken(),
            signInRequest.getVerifier());

        if (connection == null) {
            throw new OAuthVerificationException();
        }

        List<String> userIds = this.usersConnectionRepository.findUserIdsWithConnection(connection);

        String userId;
        if (userIds.size() == 0 && signInRequest.getAllowImplicitSignUp()) {
            userId = this.accountConnectionSignUp.execute(connection);
        }else if (userIds.size() > 0) {
            userId = userIds.get(0);
        }else {
            userId = null;
        }

        if (userId == null) {
            throw new SignInException();
        }else {
            this.saveConnection(connection, userId);
        }

        String jwt = this.jwtSocialSignInProvider.signIn(userId, connection, this.getRequestOrThrow());

        if (jwt == null) {
            throw new SignInException();
        }

        SignInResponse response = new SignInResponse();
        response
            .idToken(jwt)
            .user(userId)
            .providerId(provider)
            .providerUserId(connection.createData().getProviderUserId())
            .displayName(connection.getDisplayName());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<OAuth10Credentials> getConnectionV1(String provider, String user) {
        Optional<Connection<?>> connectionOpt = this.getValidConnection(provider, user);

        if (!connectionOpt.isPresent()) {
            throw new ConnectionNotFoundException();
        }

        Connection<?> connection = connectionOpt.get();

        return ResponseEntity.ok(this.getOAuthCredentials(provider, connection));
    }

    @Override
    public ResponseEntity<Void> deleteV1(String provider) {
        String userId = SecurityUtils
            .getCurrentUserId()
            .orElseThrow(UnauthorizedException::new);

        Connection<?> connection = this.getValidConnection(provider, userId)
            .orElseThrow(ConnectionNotFoundException::new);

        this.usersConnectionRepository
            .createConnectionRepository(userId)
            .removeConnection(connection.getKey());

        return ResponseEntity.ok().build();
    }
}
