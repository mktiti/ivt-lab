package hu.bme.mit.spaceship;

/**
* Defines basic spaceship functionality
* (collects just the most important ones currently)
*/
public interface SpaceShip {

  /**
  * Fires the laser or lasers of the ship
  *
  * @param firingMode how many lasers to fire
  * @return was the firing successful
  */
  public boolean fireLaser(FiringMode firingMode);

  /**
  * Fires all the torpedo stores of the ship with the specified firing mode immediately
  *
  * @param firingMode how many torpedo stores to fire
  * @return whether the fire command was successful
  */
  public boolean fireTorpedo(FiringMode firingMode);
}
