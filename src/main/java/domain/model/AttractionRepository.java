package domain.model;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;


import java.util.Set;

public interface AttractionRepository {
    Maybe<Set<Attraction>> getSavedAttractions(String id);
    Completable addAttraction(String id, String name, Double rating, int total_reviews, String url, String address);
    Completable removeAttraction(String id, String name, String address);
}
