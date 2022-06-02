package domain.model;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface AccountRepository {
    Single<Account> logIn(String username, String password);
    Completable createAccount(String name, String surname, String email, String id, String username, String password);
    //Completable logOut();
    Completable deleteAccount(String id);
    Maybe<Account> getAccountById(String id);

}
