package domain.model;

import io.reactivex.rxjava3.core.Single;

public interface AccountViewRepository {
    public Single<AccountView> getAccountDetails(String id);
}
