package controlP5;

/**
 * controlP5 is a processing gui library.
 * <p>
 * 2006-2015 by Andreas Schlegel
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 *
 * @author Andreas Schlegel (http://www.sojamo.de)
 * @modified ##date##
 * @version ##version##
 */

import java.util.*;

import controlP5.events.ReleasedOutsideListener;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.event.Event;
import processing.event.KeyEvent;


/**
 * A singleline input textfield, use arrow keys to go back and forth, use backspace to delete
 * characters. Using the up and down arrows lets you cycle through the history of the textfield.
 * <p>
 * This is the best you can get. Font handling, font switching, measuring, left align, right align,
 * etc. was giving me a big headache. not perfect, i think this is a good compromise.
 *
 * @example controllers/ControlP5textfield
 * @nosuperclasses Controller Controller
 */
public class Textfield extends Controller<Textfield> implements ReleasedOutsideListener {

    /* TODO textspacing does not work properly for bitfonts sometimes first row of pixels in a
     * bitfont texture gets cut off */

    protected boolean isTexfieldActive;
    protected boolean isKeepFocus;
    protected StringBuffer _myTextBuffer = new StringBuffer();
    protected int _myTextBufferIndex = 0;
    protected int _myTextBufferOverflow = 0;
    protected int _myTextBufferIndexPosition = 0;
    public static int cursorWidth = 1;
    protected Map<Integer, List<TextfieldCommand>> keyMapping ;

    protected InputFilter _myInputFilter = InputFilter.BITFONT;
    protected List<Integer> ignorelist;
    protected int TEXTALIGN = 0;
    protected LinkedList<String> _myHistory;
    protected int _myHistoryIndex;
    protected int len = 0;
    protected int offset = 2;
    protected int margin = 2;
    protected boolean isPasswordMode;
    protected boolean autoclear = true;
    protected int _myColorCursor = 0x88ffffff;
    protected PGraphics buffer;

    public enum InputFilter {
        INTEGER(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')), FLOAT(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.')), BITFONT(Arrays.asList('\n', '\r', ' ', '!', '"', '#', '$', '%',
                '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~')), DEFAULT(new LinkedList<Character>());

        final List<Character> allowed;

        InputFilter(List<Character> theList) {
            allowed = theList;
        }

        protected boolean apply(char theCharater) {
            if (allowed.isEmpty()) {
                return true;
            } else {
                return allowed.contains(theCharater);
            }
        }

    }

    /**
     * Convenience constructor to extend Textfield.
     *
     * @param theControlP5
     * @param theName
     * @example use/ControlP5extendController
     */
    public Textfield(ControlP5 theControlP5, String theName) {
        this(theControlP5, theControlP5.getDefaultTab(), theName, "", 0, 0, 199, 19);
        theControlP5.register(theControlP5.papplet, theName, this);
    }

    public Textfield(ControlP5 theControlP5, ControllerGroup<?> theParent, String theName, String theDefaultValue, int theX, int theY, int theWidth, int theHeight) {
        super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);

        _myCaptionLabel = new Label(cp5, theName, 0, 0, color.getCaptionLabel());
        _myValueLabel.setFont(cp5.controlFont == cp5.defaultFont ? cp5.defaultFontForText : cp5.controlFont);
        _myCaptionLabel.align(LEFT, BOTTOM_OUTSIDE);
        _myCaptionLabel.setPaddingX(0);

        _myBroadcastType = STRING;
        _myValueLabel.setFixedSize(true);
        _myValueLabel.set("");
        _myValueLabel.setWidth(getWidth() - margin * 2);
        _myValueLabel.setPadding(0, 0);
        _myValueLabel.align(LEFT, CENTER);
        _myValueLabel.setColor(color.getValueLabel());
        _myValueLabel.toUpperCase(false);

        _myValueLabel.setLabeltype(_myValueLabel.new SinglelineTextfield());

        _myHistory = new LinkedList<String>();
        _myHistory.addFirst("");

        setSize(getWidth(), getHeight());

        keyMapping = new HashMap<Integer, List<TextfieldCommand>>();
        addCommand(ENTER, new Enter());

        addCommand(DEFAULT, new InsertCharacter());
        addCommand(DELETE, new DeleteCharacter());
        addCommand(BACKSPACE, new DeleteCharacter());
        addCommand(LEFT, new MoveLeft());
        addCommand(RIGHT, new MoveRight());
        addCommand(UP, new MoveUp());
        addCommand(DOWN, new MoveDown());

        ignorelist = new LinkedList<Integer>();
        ignorelist.add(SHIFT);
        ignorelist.add(ALT);
        ignorelist.add(CONTROL);
        ignorelist.add(TAB);
        ignorelist.add(COMMANDKEY);

        setInputFilter(DEFAULT);

    }

    @Override
    public Textfield setWidth(int theWidth) {
        _myValueLabel.setWidth(theWidth);
        return super.setWidth(theWidth);
    }

    @Override
    public Textfield setHeight(int theHeight) {
        _myValueLabel.setHeight(theHeight);
        return super.setHeight(theHeight);
    }

    public Textfield setFocus(boolean theValue) {
        isTexfieldActive = isActive = theValue;
        return this;
    }

    /**
     * check if the textfield is active and in focus.
     *
     * @return boolean
     */
    public boolean isFocus() {
        return isTexfieldActive;
    }

    public Textfield keepFocus(boolean theValue) {
        isKeepFocus = theValue;
        if (isKeepFocus) {
            setFocus(true);
        }
        return this;
    }

//	public Textfield setFont( PFont thePFont ) {
//		getValueLabel( ).setFont( thePFont );
//		return this;
//	}
//
//	public Textfield setFont( ControlFont theFont ) {
//		getValueLabel( ).setFont( theFont );
//		return this;
//	}

    public Textfield setFont(int theFont) {
        getValueLabel().setFont(theFont);
        return this;
    }

    public Textfield setPasswordMode(boolean theFlag) {
        isPasswordMode = theFlag;
        return this;
    }

    public Textfield setInputFilter(int theInputType) {
        switch (theInputType) {
            case (INTEGER):
                _myInputFilter = InputFilter.INTEGER;
                break;
            case (FLOAT):
                _myInputFilter = InputFilter.FLOAT;
                break;
            case (BITFONT):
                _myInputFilter = InputFilter.BITFONT;
                break;
            default:
                _myInputFilter = InputFilter.DEFAULT;
                break;
        }
        return this;
    }

    @Override
    public Textfield setValue(float theValue) {
        // use setText(String) instead
        return this;
    }

    @Override
    protected void updateFont(ControlFont theControlFont) {
        super.updateFont(theControlFont);
    }

    public Textfield setValue(String theText) {
        _myTextBuffer = new StringBuffer(theText);
        setIndex(_myTextBuffer.length());
        return this;
    }

    public Textfield setText(String theText) {
        return setValue(theText);
    }

    public Textfield clear() {
        // create a new text buffer
        _myTextBuffer = new StringBuffer();
        // reset the buffer index
        setIndex(0);
        return this;
    }

    public Textfield setAutoClear(boolean theValue) {
        autoclear = theValue;
        return this;
    }

    public boolean isAutoClear() {
        return autoclear;
    }

    @Override
    protected void mousePressed() {
        super.mousePressed();

        if (isActive) {
            // TODO System.out.println("adjust cursor");
        }
        int x = (int) (getControlWindow().mouseX - x(getAbsolutePosition()));
        int y = (int) (getControlWindow().mouseY - y(getAbsolutePosition()));

        // TODO System.out.println(x + ":" + y);
        setFocus(true);


    }

    @Override
    public void mouseReleasedOutside() {
        if (isKeepFocus == false) {
            isTexfieldActive = isActive = false;
        }
    }

    public int getIndex() {
        return _myTextBufferIndex;
    }

    public String getText() {
        return _myTextBuffer.toString();
    }

    //get keymappings
    public Map<Integer, List<TextfieldCommand>> getKeyMappings() {
        return keyMapping;
    }


    public Textfield setColor(int theColor) {
        getValueLabel().setColor(theColor);
        return this;
    }

    public Textfield setColorCursor(int theColor) {
        _myColorCursor = theColor;
        return this;
    }

    @Override
    public Textfield setSize(int theWidth, int theHeight) {
        super.setSize(theWidth, theHeight);
        buffer = cp5.papplet.createGraphics(getWidth(), getHeight());
        return this;
    }

    @Override
    public void draw(PGraphics theGraphics) {

        theGraphics.pushStyle();
        theGraphics.fill(color.getBackground());
        theGraphics.pushMatrix();
        theGraphics.translate(x(position), y(position));
        theGraphics.noStroke();
        theGraphics.rect(0, 0, getWidth(), getHeight());

        theGraphics.fill(_myColorCursor);
        theGraphics.pushMatrix();
        theGraphics.pushStyle();

        buffer.beginDraw();
        buffer.background(0, 0);
        //draw border
        if (drawBorder) {
            theGraphics.strokeWeight(_myBorderWidth);
            theGraphics.stroke(color.getForeground());
            //draw 4 lines around the edges
            theGraphics.strokeCap(4);
            theGraphics.line(0, 0, getWidth(), 0);
            theGraphics.line(getWidth(), 0, getWidth(), getHeight());
            theGraphics.line(getWidth(), getHeight(), 0, getHeight());
            theGraphics.line(0, getHeight(), 0, 0);

        }


        final String text = passCheck(getText());
        final int textWidth = ControlFont.getWidthFor(text.substring(0, _myTextBufferIndex), _myValueLabel, buffer);
        final int dif = PApplet.max(textWidth - _myValueLabel.getWidth(), 0);
        final int _myTextBufferIndexPosition = ControlFont.getWidthFor(text.substring(0, _myTextBufferIndex), _myValueLabel, buffer);
        _myValueLabel.setText(text);

        if (TEXTALIGN == 0) { // Left align.
            _myValueLabel.draw(buffer, -dif , 0, this);
            buffer.noStroke();

        } else if (TEXTALIGN == 1) { // Center align.
            int centerPoint = getWidth() / 2;
            int textShift = centerPoint - textWidth / 2;
            _myValueLabel.draw(buffer, textShift, 0, this);
            buffer.noStroke();

        }

        if (isTexfieldActive) {
            if (!cp5.papplet.keyPressed) {
                buffer.fill(_myColorCursor, PApplet.abs(PApplet.sin(cp5.papplet.frameCount * 0.05f)) * 255);
            } else {
                buffer.fill(_myColorCursor);
            }
            // we insert 3 char worth of spacing
            int cursorX = _myTextBufferIndexPosition ;
            cursorX = Math.min(cursorX,_myValueLabel.getWidth()-1);
            int cursorY = 0;
            int cursorWidth = 1;
            int cursorHeight = getHeight();
            if(_myValueLabel.isMultiline){
                cursorHeight = _myValueLabel.getLineHeight();
            }

            buffer.rect(cursorX, cursorY, cursorWidth, cursorHeight);
        }


        buffer.endDraw();
        theGraphics.image(buffer, 0, 0);

        theGraphics.popStyle();
        theGraphics.popMatrix();

        theGraphics.fill(isTexfieldActive ? color.getActive() : color.getForeground());
        //theGraphics.rect( 0 , 0 , getWidth( ) , 1 );
        //theGraphics.rect( 0 , getHeight( ) - 1 , getWidth( ) , 1 );
        //theGraphics.rect( -1 , 0 , 1 , getHeight( ) );
        //theGraphics.rect( getWidth( ) , 0 , 1 , getHeight( ) );
        _myCaptionLabel.draw(theGraphics, 0, 0, this);
        theGraphics.popMatrix();
        theGraphics.popStyle();
    }

    private String passCheck(String label) {
        if (!isPasswordMode) {
            return label;
        }
        String newlabel = "";
        for (int i = 0; i < label.length(); i++) {
            newlabel += "*";
        }
        return newlabel;
    }

    @Override
    public void keyEvent(KeyEvent theKeyEvent) {
        if (shouldSkipNextEvent) {
            shouldSkipNextEvent = false;
            return;
        }
        if (isUserInteraction && isTexfieldActive && isActive && theKeyEvent.getAction() == KeyEvent.PRESS) {
            if (ignorelist.contains(cp5.getKeyCode())) {
                return;
            }
            int keyCode = cp5.getKeyCode();
            if (keyMapping.containsKey(keyCode)) {
                for (TextfieldCommand command : keyMapping.get(keyCode)) {
                    command.execute();
                }
            } else {
                keyMapping.get(DEFAULT).get(0).execute();
            }
        }
    }


    /**
     * make the controller execute a return event. submit the current content of the texfield.
     */


    public String[] getTextList() {
        String[] s = new String[_myHistory.size()];
        _myHistory.toArray(s);
        return s;
    }

    protected Textfield setIndex(int theIndex) {
        _myTextBufferIndex = theIndex;
        return this;
    }


    public void addCommand(int keyCode, TextfieldCommand command) {
        if (!keyMapping.containsKey(keyCode)) {
            keyMapping.put(keyCode, new ArrayList<>());
        }
        keyMapping.get(keyCode).add(command);
    }

    class InsertCharacter implements TextfieldCommand {

        public void execute() {
            if ((int) (cp5.getKey()) == 65535) {
                return;
            }

            if (_myInputFilter.apply(cp5.getKey())) {
                _myTextBuffer.insert(_myTextBufferIndex, (char) cp5.getKey());
                setIndex(_myTextBufferIndex + 1);
            }
        }
    }

    class Enter implements TextfieldCommand {

        public void execute() {
            setStringValue(_myTextBuffer.toString());
            broadcast();
            // update current buffer with the last item inside the input history
            _myHistory.set(_myHistory.size() - 1, _myTextBuffer.toString());
            // set the history index to our last item
            _myHistoryIndex = _myHistory.size();
            // add a new and empty buffer to the history
            _myHistory.add("");
            if (autoclear) {
                clear();
            }
        }
    }

    class DeleteCharacter implements TextfieldCommand {

        public void execute() {
            if (_myTextBuffer.length() > 0 && _myTextBufferIndex > 0) {
                _myTextBuffer.deleteCharAt(_myTextBufferIndex - 1);
                setIndex(_myTextBufferIndex - 1);
            }
        }
    }

    class MoveLeft implements TextfieldCommand {

        public void execute() {
            setIndex(((cp5.modifiers & Event.META) > 0) ? 0 : PApplet.max(0, _myTextBufferIndex - 1));
        }
    }

    class MoveRight implements TextfieldCommand {

        public void execute() {
            setIndex(((cp5.modifiers & Event.META) > 0) ? _myTextBuffer.length() : PApplet.min(_myTextBuffer.length(), _myTextBufferIndex + 1));
        }
    }

    class MoveUp implements TextfieldCommand {

        public void execute() {
            if (_myHistoryIndex == 0) {
                return;
            }
            _myHistoryIndex = PApplet.max(0, --_myHistoryIndex);
            _myTextBuffer = new StringBuffer(_myHistory.get(_myHistoryIndex));
            setIndex(_myTextBuffer.length());
        }
    }

    class MoveDown implements TextfieldCommand {

        public void execute() {
            if (_myHistoryIndex >= _myHistory.size() - 1) {
                return;
            }

            _myHistoryIndex = PApplet.min(_myHistory.size() - 1, ++_myHistoryIndex);
            _myTextBuffer = new StringBuffer(_myHistory.get(_myHistoryIndex));
            setIndex(_myTextBuffer.length());
        }
    }

}