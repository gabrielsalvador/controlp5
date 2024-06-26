package controlP5;

/**
 * controlP5 is a processing gui library.
 * 
 * 2006-2015 by Andreas Schlegel
 * 
 * This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at
 * your option) any later version. This library is
 * distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 * 
 * @author Andreas Schlegel (http://www.sojamo.de)
 * @modified ##date##
 * @version ##version##
 * 
 */

import processing.core.PGraphics;
import processing.core.PImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * <p>
 * A button triggers an event after it has been release.
 * Events can be linked to functions and fields inside your
 * program/sketch. for a full documentation of this
 * controller see the {@link Controller} class.
 * 
 * @example controllers/ControlP5button
 */

public class Button extends Controller< Button >  {

	protected boolean isPressed;
	protected boolean isOn = false;
	public static int autoWidth = 69;
	public static int autoHeight = 19;
	protected int activateBy = RELEASE;
	protected boolean isSwitch = false;

	/**
	 * Convenience constructor to extend Button.
	 *
	 * @example use/ControlP5extendController
	 */
	public Button( ControlP5 theControlP5 , String theName ) {
		this( theControlP5 , theControlP5.getDefaultTab( ) , theName , 0 , 0 , 0 , autoWidth , autoHeight );
		theControlP5.register( theControlP5.papplet , theName , this );
	}

	protected Button( ControlP5 theControlP5 , ControllerGroup< ? > theParent , String theName , float theDefaultValue , int theX , int theY , int theWidth , int theHeight ) {
		super( theControlP5 , theParent , theName , theX , theY , theWidth , theHeight );
		_myValue = theDefaultValue;
		_myCaptionLabel.align( CENTER , CENTER );
	}

	/**
	 * @exclude
	 */
	public Button( ) {
		super( null , null , null , 0 , 0 , 1 , 1 );
	}

	@Override protected void onEnter( ) {
		isActive = true;
	}

	@Override protected void onLeave( ) {
		isActive = false;
		setIsInside( false );
	}

	/**
	 * @exclude
	 */
	@Override @ControlP5.Invisible public void mousePressed( ) {
		isActive = getIsInside( );
		isPressed = true;
		if ( activateBy == PRESSED ) {
			activate( );
		}
	}

	/**
	 * @exclude
	 */
	@Override @ControlP5.Invisible public void mouseReleased( ) {
		isPressed = false;
		if ( activateBy == RELEASE ) {
			activate( );
		}
		isActive = false;
	}

	/**
	 * A button can be activated by a mouse PRESSED or mouse
	 * RELEASE. Default value is RELEASE.
	 *
	 * @param theValue use ControlP5.PRESSED or
	 *            ControlP5.RELEASE as parameter
	 * @return Button
	 */
	public Button activateBy( int theValue ) {
		if ( theValue == PRESS ) {
			activateBy = PRESS;
		} else {
			activateBy = RELEASE;
		}
		return this;
	}

	protected void activate( ) {
		if ( isActive ) {
			isActive = false;
			isOn = !isOn;
			setValue( _myValue );
		}
	}

	/**
	 * @exclude
	 */
	@Override @ControlP5.Invisible public void mouseReleasedOutside( ) {
		mouseReleased( );
	}



	/**
	 * {@inheritDoc}
	 */
	@Override public Button setValue( float theValue ) {
		_myValue = theValue;
		broadcast( FLOAT );
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public Button update( ) {
		return setValue( _myValue );
	}

	/**
	 * Turns a button into a switch, or use a Toggle
	 * instead.
	 *
	 */
	public Button setSwitch( boolean theFlag ) {
		isSwitch = theFlag;
		if ( isSwitch ) {
			_myBroadcastType = BOOLEAN;
		} else {
			_myBroadcastType = FLOAT;
		}
		return this;
	}

	/**
	 * If the button acts as a switch, setOn will turn on
	 * the switch. Use
	 * {@link Button#setSwitch(boolean) setSwitch}
	 * to turn a Button into a Switch.
	 *
	 * @return Button
	 */

	/**
	 * sets the state of isOn only, no event propagations
	 * @param state
	 * @return
	 */
	public Button updateOn(boolean state){
		isOn = state;
		return this;
	}
	public Button setOn( ) {
		if ( isSwitch ) {
			isOn = false;
			isActive = true;
			activate( );
		}
		return this;
	}

	/**
	 * If the button acts as a switch, setOff will turn off
	 * the switch. Use
	 * {@link Button#setSwitch(boolean) setSwitch}
	 * to turn a Button into a Switch.
	 *
	 * @return Button
	 */
	public Button setOff( ) {
		if ( isSwitch ) {
			isOn = true;
			isActive = true;
			activate( );
		}
		return this;
	}

	/**
	 * @return boolean
	 */
	public boolean isOn( ) {
		return isOn;
	}

	public boolean isSwitch( ) {
		return isSwitch;
	}

	/**
	 * @return boolean
	 */
	public boolean isPressed( ) {
		return isPressed;
	}

	/**
	 * Returns true or false and indicates the switch state
	 * of the button. {@link setSwitch(boolean) setSwitch}
	 * should have been set before.
	 *
	 * @see Button#setSwitch(boolean)
	 * @return boolean
	 */
	public boolean getBooleanValue( ) {
		return isOn;
	}

	/**
	 * @exclude
	 */
	@Override @ControlP5.Invisible public Button updateDisplayMode( int theMode ) {
		return updateViewMode( theMode );
	}

	/**
	 * @exclude
	 */
	@ControlP5.Invisible public Button updateViewMode( int theMode ) {
		_myDisplayMode = theMode;
		switch ( theMode ) {
			case ( DEFAULT ):
				_myControllerView = new ButtonView( );
				break;
			case ( IMAGE ):
				_myControllerView = new ButtonImageView( );
				break;
			case ( CUSTOM ):
			default:
				break;

		}
		return this;
	}



	private class ButtonView implements ControllerView< Button > {

		public void display( PGraphics theGraphics , Button theController ) {
			theGraphics.noStroke( );
			if ( isOn && isSwitch ) {
				theGraphics.fill( color.getActive( ) );
			} else {
				if ( getIsInside( ) ) {
					if ( isPressed ) {
						theGraphics.fill( color.getActive( ) );
					} else {
						theGraphics.fill( color.getForeground( ) );
					}
				} else {
					theGraphics.fill( color.getBackground( ) );
				}
			}
			theGraphics.rect( 0 , 0 , getWidth( ) , getHeight( ) );
			if ( isLabelVisible ) {

				_myCaptionLabel.draw( theGraphics , 0 , 0 , theController );
				
			}
		}
	}

	private class ButtonImageView implements ControllerView<Button> {

		public void display(PGraphics theGraphics, Button theController) {
			theGraphics.pushStyle();
			PImage imageToDraw;
			int backgroundColor = 0;

			if (isOn && isSwitch) {
				imageToDraw = (availableImages[HIGHLIGHT]) ? images[HIGHLIGHT] : images[DEFAULT];
				backgroundColor = color.getActive(); // Background color for ACTIVE state
			} else if (getIsInside()) {
				if (isPressed) {
					imageToDraw = (availableImages[ACTIVE]) ? images[ACTIVE] : images[DEFAULT];
					backgroundColor = color.getBackground(); // Background color for ACTIVE state
				} else {
					imageToDraw = (availableImages[OVER]) ? images[OVER] : images[DEFAULT];
					backgroundColor = color.getForeground(); // Foreground color for OVER state
				}
			} else {
				imageToDraw = images[DEFAULT];

			}

			if(backgroundColor != 0) theGraphics.fill(backgroundColor);
			theGraphics.rect(0, 0, getWidth(), getHeight());

			// Calculate the position to center the image
			float xPos = (theController.getWidth() - imageToDraw.width) / 2.0f;
			float yPos = (theController.getHeight() - imageToDraw.height) / 2.0f;

			// Draw the image at the calculated position
			theGraphics.image(imageToDraw, xPos, yPos);
			theGraphics.popStyle();
		}
	}

	/**
	 * @exclude
	 */
	@Override public String getInfo( ) {
		return "type:\tButton\n" + super.getInfo( );
	}

	/**
	 * @exclude
	 */
	@Override public String toString( ) {
		return super.toString( ) + " [ " + getValue( ) + " ] " + "Button" + " (" + this.getClass( ).getSuperclass( ) + ")";
	}

}