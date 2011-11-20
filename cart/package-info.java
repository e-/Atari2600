/** Contains a variety of cartridge types.
 * The Atari 2600 cartridges used a variety of different hardware layouts (inside the cartridge),
 * and thus, different classes are needed to represent the different types...e.g. some cartridges contained
 * RAM chips that extended the total amount of RAM available (for that game).   
 * The Cartridge class is the abstract base class for all of the different types,
 *and it contains a static method that is responsible for calling the appropriate constructor of 
 *the derivative class.  
 *
 *
 */
package jstella.cart;
