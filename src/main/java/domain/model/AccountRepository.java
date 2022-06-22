package domain.model;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.util.UUID;

public interface AccountRepository {
    Single<Account> logIn(String username, String password);
    Completable createAccount(String name, String surname, String email, UUID id, String username, String password);
    //Completable logOut();
    Completable deleteAccount(UUID id);
    Maybe<Account> getAccountById(UUID id);

}
