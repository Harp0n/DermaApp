package com.example.dermaapp.ObserverPattern;

public interface IObservable {
    boolean addObserver(IObserver observer);

    boolean removeObserver(IObserver observer);

    void notifyObservers(String response);
}
