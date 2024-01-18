/*
 * Java
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.demo.smart_thermostat.common;

import static com.microej.demo.smart_thermostat.common.Context.State.INSIDE;
import static com.microej.demo.smart_thermostat.common.Context.State.TRANSITION_INSIDE;

/**
 * The context manages the current application state.
 */
public final class Context {

	public static final Context INSTANCE = new Context();
	private State current;
	private State previous;

	/**
	 * Avoid outside instantiation.
	 */
	private Context() {
		this.current = State.HOME;
		this.previous = State.HOME;
	}

	/**
	 * Sets the new state of the application. Also saves the previous state.
	 */
	public void setState(State newState) {
		this.previous = this.current;
		this.current = newState;
	}

	/**
	 * Returns the current state of the application.
	 */
	public State getCurrentState() {
		return this.current;
	}

	/**
	 * Returns true if the application is in any transition state.
	 *
	 * @return {@code true}, if we are currently in a transition state.
	 */
	public boolean inTransition() {
		return (this.current.equals(State.TRANSITION_HOME)) || (this.current.equals(State.TRANSITION_OUTSIDE))
				|| (this.current.equals(State.TRANSITION_INSIDE));
	}

	/**
	 * Returns true if the application is coming to or going from the inside page.
	 */
	public boolean isComingToOrGoingFromInside() {
		return (this.current.equals(INSIDE) || this.previous.equals(TRANSITION_INSIDE)
				|| this.current.equals(TRANSITION_INSIDE) || this.previous.equals(INSIDE));
	}

	/**
	 * Provides information about the current application state.
	 */
	public enum State {
		HOME, // At home.
		INSIDE, // At the inside page.
		OUTSIDE, // At the outside page.
		TRANSITION_INSIDE, // Going to the inside page.
		TRANSITION_OUTSIDE, // Going to the outside page.
		TRANSITION_HOME // Going home.
	}
}