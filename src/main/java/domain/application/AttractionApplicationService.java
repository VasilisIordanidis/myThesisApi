package domain.application;

import domain.application.command.AddAttractionCommand;
import domain.application.command.RemoveAttractionCommand;
import domain.application.query.GetSavedAttractionsQuery;
import domain.model.AccountRepository;
import domain.model.Attraction;
import domain.model.AttractionRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

import java.util.HashSet;
import java.util.Set;

public class AttractionApplicationService {
    private final AttractionRepository attractionRepository;
    private final AccountRepository accountRepository;

    public AttractionApplicationService(AttractionRepository attractionRepository, AccountRepository accountRepository) {
        this.attractionRepository = attractionRepository;
        this.accountRepository = accountRepository;
    }

    public Maybe<Set<Attraction>> execute(GetSavedAttractionsQuery query){
        return accountRepository.getAccountById(query.getAccountId())
                .isEmpty()
                .flatMapMaybe(empty -> {
                   if(empty){
                       return Maybe.error(new Throwable("Account doesn't exist"));
                   } else {
                       return attractionRepository.getSavedAttractions(query.getAccountId())
                               //.switchIfEmpty(Completable.error(new Throwable("Account doesn't exist")).toMaybe())
                               .flatMap(attractions -> {
                                  if (attractions.size()==0){
                                      return Maybe.just(new HashSet<>());
                                  } else {
                                      return Maybe.just(attractions);
                                  }
                               });
                   }
                });
    }

    public Completable execute(AddAttractionCommand command){
        return accountRepository.getAccountById(command.getAccountId())
                .isEmpty()
                .flatMapCompletable(empty -> {
                    if(empty){
                        return Completable.error(new Throwable("Account doesn't exist"));
                    } else {
                        return attractionRepository.getSavedAttractions(command.getAccountId())
                                .flatMapCompletable(attractionSet -> {
                                    //Attraction attraction = new Attraction()
                                    return attractionRepository.addAttraction(command.getAccountId(), command.getAttractionName(), command.getRating(), command.getTotalReviews(), command.getImgUrl(), command.getLocation());
                                });
                    }
                });
    }

    public Completable execute(RemoveAttractionCommand command){
        return accountRepository.getAccountById(command.getAccountId())
                .isEmpty()
                .flatMapCompletable(empty -> {
                    if(empty){
                        return Completable.error(new Throwable("Account doesn't exist"));
                    } else {
                        return attractionRepository.getSavedAttractions(command.getAccountId())
                                .isEmpty()
                                .flatMapCompletable(emptySet -> {
                                    if(emptySet){
                                        return Completable.error(new Throwable("No saved attractions"));
                                    } else {
                                        return attractionRepository.removeAttraction(command.getAccountId(), command.getAttractionName(), command.getAttractionLocation());
                                    }
                                });
                    }
                });
    }
}
