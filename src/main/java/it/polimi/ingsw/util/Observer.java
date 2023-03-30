package it.polimi.ingsw.util;

/**
 * A class can implement the {@code Observer} interface when it
 * wants to be informed of changes in observable objects.
 *
 * @see     it.polimi.ingegneriaDelSoftware.provaFinale.esercitazioni.util.Observable
 *
 * @param <ArgType> the type of the argument passed to the {@code update} method of the observers
 *
 * @implNote
 * This class is a Generic Implementation of the deprecated {@link java.util.Observer}.
 */
public interface Observer<> {
    /**
     * This method is called whenever the observed object is changed. An
     * application calls an {@code Observable} object's
     * {@code notifyObservers} method to have all the object's
     * observers notified of the change.
     *
     * @param   o     the observable object.
     * @param   arg   an argument passed to the {@code notifyObservers}
     *                 method.
     */

    void update(SubjectType o, ArgType arg);
}
