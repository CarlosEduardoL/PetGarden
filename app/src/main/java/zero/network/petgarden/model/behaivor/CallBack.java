package zero.network.petgarden.model.behaivor;

/**
 * @author CarlosEduardoL
 * Callback, as it name says, is the class used when a method is Async and need return something
 * @param <T> is the return type
 */
public interface CallBack<T> {
    /**
     * this method is called when the method is ready to return
     * @param result is the return value of the function, is called async
     */
    void onResult(T result);
}
