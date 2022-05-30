package domain.model;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

public interface AccountRepository {
    Maybe<Account> logIn(String username, String password);
    Completable createAccount(String name, String surname, String email, String id, String username, String password);
    //Completable logOut();
}
