package utils;

import java.util.function.Function;

public class Pair<F, S> {

    private final F first;
    private final S second;

    public Pair(final F first, final S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public <F2> Pair<F2, S> mapFirst(final Function<? super F, ? extends F2> function) {
        return of(function.apply(first), second);
    }

    public <S2> Pair<F, S2> mapSecond(final Function<? super S, ? extends S2> function) {
        return of(first, function.apply(second));
    }

    public static <F, S> Pair<F, S> of(final F first, final S second) {
        return new Pair<>(first, second);
    }
}
