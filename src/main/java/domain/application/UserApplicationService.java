package domain.application;

import domain.application.command.CreateUserCommand;
import domain.application.command.DeleteUserCommand;
import domain.application.query.LogInQuery;
import domain.model.AccountRepository;
import domain.model.AccountView;
import domain.model.AccountViewRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import java.util.UUID;

public class UserApplicationService {
    private final AccountRepository accountRepository;
    private final AccountViewRepository accountViewRepository;

    public UserApplicationService(AccountRepository accountRepository, AccountViewRepository accountViewRepository) {
        this.accountRepository = accountRepository;
        this.accountViewRepository = accountViewRepository;
    }

    public Single<AccountView> execute(LogInQuery query) {
        return this.accountRepository.logIn(query.getUsername(), query.getPassword())
                .concatMap(account -> {
                    System.out.println(account.toString());
                    return accountViewRepository.getAccountDetails(account.getUuid());
                });
    }

    public Completable execute(CreateUserCommand command) {
        return this.accountRepository.getAccountById(UUID.fromString(command.getAccountId()))
                .isEmpty()
                .flatMapCompletable(empty -> {
                    if (!empty) {
                        return Completable.error(new Throwable("User already exists"));
                    } else {
                        return this.accountRepository.createAccount(command.getFirstName(), command.getLastName(), command.getEmail(), UUID.fromString(command.getAccountId()), command.getUsername(), command.getPassword());
                    }
                });
    }

    public Completable execute(DeleteUserCommand command) {
        return this.accountRepository.getAccountById(UUID.fromString(command.getId()))
                .isEmpty()
                .flatMapCompletable(empty -> {
                    if (empty) {
                        return Completable.error(new Throwable("User does not exist"));
                    } else {
                        return this.accountRepository.deleteAccount(UUID.fromString(command.getId()));
                    }
                });
    }
}
