package org.mifort.rxjava;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RxJavaTest {

    @Test
    void ObservableJustTest() {

        Observable<String> observable = Observable.just("Hello");

        AtomicReference<String> result = new AtomicReference<>();
        observable.subscribe(result::set);

        assertEquals("Hello", result.get());
    }

    @Test
    void onEventTest() {
        String[] letters = {"m", "i", "f", "o", "r", "t"};
        Observable<String> observable = Observable.fromArray(letters);
        AtomicReference<String> result = new AtomicReference<>("");
        observable.subscribe(
                i -> result.set(result.get().concat(i)),  //OnNext
                Throwable::printStackTrace, //OnError
                () -> result.set(result.get().concat("_Completed"))  //OnCompleted
        );
        assertEquals("mifort_Completed", result.get());
    }

    @Test
    void MapTest() {
        String[] letters = {"m", "i", "f", "o", "r", "t"};
        AtomicReference<String> result = new AtomicReference<>("");
        Observable.fromArray(letters)
                .map(String::toUpperCase)
                .subscribe(letter -> result.set(result.get().concat(letter.toUpperCase(Locale.ROOT))));
        assertEquals("MIFORT", result.get());

    }

    @Test
    void flatMapTest() {
        AtomicReference<String> result = new AtomicReference<>("");
        Observable.just("book1", "book2")
                .flatMap(s -> getTitle())
                .subscribe(l -> result.set(result.get().concat(l)));

        assertEquals("titletitle", result.get());
    }

    @Test
    void groupByTest() {
        Integer[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] EVEN = new String[1];
        String[] ODD = new String[1];

        Observable.fromArray(numbers)
                .groupBy(i -> (0 == (i % 2)) ? "EVEN" : "ODD")
                .subscribe(group ->
                        group.subscribe((number) -> {
                            if (Objects.equals(group.getKey(), "EVEN")) {
                                EVEN[0] += number;
                            } else {
                                ODD[0] += number;
                            }
                        })
                );
        assertEquals("0246810", EVEN[0]);
        assertEquals("13579", ODD[0]);

        AtomicReference<String> result = new AtomicReference<>("");
        Observable.fromArray(numbers)
                .filter(i -> (i % 2 == 1))
                .subscribe(i -> result.set(result.get().concat(i.toString())));

        assertEquals("13579", result.get());
    }

    @Test
    void publishSubjectTest() {
        Subscriber subscriber = new Subscriber();

        PublishSubject<Integer> subject = PublishSubject.create();
        subject.subscribe(subscriber.getFirstObserver());
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);
        subject.subscribe(subscriber.getSecondObserver());
        subject.onNext(4);
        subject.onComplete();

        assertEquals(14, subscriber.getValue1() + subscriber.getValue2());
    }


    private Observable<String> getTitle() {
        String[] titleList = {"title"};
        return Observable.fromArray(titleList);
    }

}
