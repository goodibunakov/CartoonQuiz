package ru.goodibunakov.cartoonquiz;

public interface LoadingTaskFinishedListener {
    void onTaskFinished(MainActivityFragment2.Result result); // If you want to pass something back to the listener add a param to this method
}
