package it.unimib.disco.bigtwine.services.socials.connect;

public interface AccountCreatorLocator {
    <A> AccountCreator<A> getAccountCreator(Class<A> apiClass);
    <A> AccountCreator<A> getAccountCreator(A api);
    <A> void addAccountCreator(Class<A> api, AccountCreator<A> creator);
    <A> void removeAccountCreator(Class<A> apiClass);
}
