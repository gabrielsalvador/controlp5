  /**
* ControlP5 Tooltip
*
* Broken with version 2.2.1+
*
* add a tooltip to a controller.
* hover your mouse on top of a slider in the example and wait 
* for 1 second for the tooltip to appear.
*
* find a list of public methods available for the Tooltip Controller
* at the bottom of this sketch.
*
* NOTE: currently does not work for custom PGraphics context
*
* by Andreas Schlegel, 2011
* www.sojamo.de/libraries/controlp5
*
*/

import controlP5.*;


ControlP5 cp5;

float s1 = 20, s2 = 100;

void setup() {
  size(700,400);
  cp5 = new ControlP5(this);
  
  
  
  
  
  // sorry, Tooltip is currently not working
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  cp5.begin(100,100);
  cp5.addSlider("s1",10,200).linebreak();
  cp5.addSlider("s2",0,150);
  cp5.end();
  
  cp5.getTooltip().setDelay(500);
  cp5.getTooltip().register("s1","Changes the size of the ellipse.");
  cp5.getTooltip().register("s2","Changes the Background");
  
  smooth();
  noStroke();
}


void keyPressed() {
  println("unregistering the tooltip for s2");
  cp5.getTooltip().unregister("s2");
}
void draw() {
  background(s2);
  fill(255,100);
  ellipse(width/2, height/2, s1,s1);
}



/*
a list of all methods available for the Tooltip Controller
use ControlP5.printPublicMethodsFor(Tooltip.class);
to print the following list into the console.

You can find further details about class Tooltip in the javadoc.

Format:
ClassName : returnType methodName(parameter type)


main.java.controlp5.Tooltip : Label getLabel()
main.java.controlp5.Tooltip : Tooltip disable()
main.java.controlp5.Tooltip : Tooltip enable()
main.java.controlp5.Tooltip : Tooltip register(Controller, String)
main.java.controlp5.Tooltip : Tooltip register(String, String)
main.java.controlp5.Tooltip : Tooltip setAlpha(int)
main.java.controlp5.Tooltip : Tooltip setBorder(int)
main.java.controlp5.Tooltip : Tooltip setColorBackground(int)
main.java.controlp5.Tooltip : Tooltip setColorLabel(int)
main.java.controlp5.Tooltip : Tooltip setDelay(long)
main.java.controlp5.Tooltip : Tooltip setHeight(int)
main.java.controlp5.Tooltip : Tooltip setLabel(Label)
main.java.controlp5.Tooltip : Tooltip setPositionOffset(float, float)
main.java.controlp5.Tooltip : Tooltip setView(ControllerView)
main.java.controlp5.Tooltip : Tooltip setWidth(int)
main.java.controlp5.Tooltip : Tooltip unregister(Controller)
main.java.controlp5.Tooltip : Tooltip unregister(String)
main.java.controlp5.Tooltip : boolean isEnabled()
main.java.controlp5.Tooltip : int getBorder()
main.java.controlp5.Tooltip : int getWidth()
java.lang.Object : String toString() 
java.lang.Object : boolean equals(Object) 

created: 2015/03/24 12:21:36

*/


