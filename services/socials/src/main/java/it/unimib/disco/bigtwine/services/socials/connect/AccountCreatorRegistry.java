package it.unimib.disco.bigtwine.services.socials.connect;

import it.unimib.disco.bigtwine.services.socials.connect.support.BasicFullNameSplitter;
import org.springframework.social.twitter.api.Twitter;

import java.util.HashMap;
import java.util.Map;


public final class AccountCreatorRegistry implements AccountCreatorLocator {

    private final Map<Class<?>, AccountCreator> creatorsIndex = new HashMap<>();

    public AccountCreatorRegistry() {
        this.registerDefaultCreators();
    }

    private void registerDefaultCreators() {
        this.addAccountCreator(
            Twitter.class,
            new TwitterAccountCreator(new BasicFullNameSplitter()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> AccountCreator<A> getAccountCreator(Class<A> apiClass) {
        Class<?> klass = this.creatorsIndex
            .keySet()
            .stream()
            .filter((k) -> k.isAssignableFrom(apiClass))
            .findFirst()
            .orElse(null);

        if (klass != null) {
            return (AccountCreator<A>) this.creatorsIndex.get(klass);
        }else {
            return new DefaultAccountCreator<>();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> AccountCreator<A> getAccountCreator(A api) {
        return this.getAccountCreator((Class<A>)api.getClass());
    }

    @Override
    public <A> void addAccountCreator(Class<A> apiClass, AccountCreator<A> creator) {
        this.creatorsIndex.put(apiClass, creator);
    }

    @Override
    public <A> void removeAccountCreator(Class<A> apiClass) {
        this.creatorsIndex.remove(apiClass);
    }

}
