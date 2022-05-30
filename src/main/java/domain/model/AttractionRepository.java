package domain.model;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;


import java.util.Set;

public interface AttractionRepository {
    Maybe<Set<Attraction>> getSavedAttractions(String id);
    Completable addAttraction();
    Completable removeAttraction();
}
