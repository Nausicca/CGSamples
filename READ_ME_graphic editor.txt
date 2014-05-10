
Programs have been executed and tested in eclipse environment
Please place node.gif and link.gif in the same file path as GraphicEditor.java before execution.

The details of the editor are as follows:
-----------------------------------------

 The graphic editor has all the features of the described problem.
 * There is a dropdown menu to select options.
 * Pythogoras:
 * ------------
 * If Pythogoras is chosen, a pythogoras tree is drawn,with the given number of steps.
 * There is a default value for the number of steps which is 10.
 * Graph Mode:
 * ----------
 * If graph is chosen,we need to click on a random point in the panel area and then click on 'node' button.
 * This will generate a node in the selected position.
 * Now same process can be repeated to draw another node.
 * Once nodes are drawn,'link' button has to be selected.
 * Now,in connection mode, if we drag the mouse from source to destination node ,making sure that we click within the nodes,a link
 * will be drawn.
 * An error message will be displayed if clicked anywhere else.
 * 
 * A 'Clear' button is provided to clear screen contents.Quit will exit and close the window.
 * 
 * Implementation:
   ---------------
 * 5 Separate panels were used.Action listeners were used for each button and combo boxes.
 * The images files for node and link have to placed in the same path.
 * Pythogoras tree is drawn with the standard algorithm,based on height and width and the 3 co-ordinates are computed recursively
   depending on the step size.
 * For drawing nodes and interconnecting them,mouse position is captured through different mouse events and is used to draw
   for the appropriate co-ordinates.