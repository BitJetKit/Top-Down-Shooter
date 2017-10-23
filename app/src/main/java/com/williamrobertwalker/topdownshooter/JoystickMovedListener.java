package com.williamrobertwalker.topdownshooter;

public interface JoystickMovedListener {

    public void OnMoved(int pan, int tilt);

    public void OnReleased();

}