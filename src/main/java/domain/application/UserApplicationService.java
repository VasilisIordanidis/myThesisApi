package domain.application;

import domain.application.command.CreateUserCommand;
import domain.application.command.DeleteUserCommand;
import domain.model.Account;
import domain.model.AccountRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

public class UserApplicationService {
    private final AccountRepository accountRepository;

    public UserApplicationService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Completable execute(CreateUserCommand command){
        return this.accountRepository.getAccountById(command.getAccountId())
                .isEmpty()
                .flatMapCompletable(empty -> {
                    if(!empty){
                        return Completable.error(new Throwable("User already exists"));
                    } else {
                        return this.accountRepository.createAccount(command.getFirstName(), command.getLastName(), command.getEmail(), command.getAccountId(), command.getUsername(), command.getPassword());

                    }
                });
    }

    public Completable execute(DeleteUserCommand command){
        return this.accountRepository.getAccountById(command.getId())
                .isEmpty()
                .flatMapCompletable(empty -> {
                    if(empty){
                        return Completable.error(new Throwable("User does not exist"));
                    } else {
                        return this.accountRepository.deleteAccount(command.getId());
                    }
                });
    }
}
