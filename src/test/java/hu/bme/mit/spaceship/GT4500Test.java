package hu.bme.mit.spaceship;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

public class GT4500Test {

  private GT4500 ship;

  private TorpedoStore mockPrimaryStore;
  private TorpedoStore mockSecondaryStore;

  @Before
  public void init(){
    mockPrimaryStore = mock(TorpedoStore.class);
    mockSecondaryStore = mock(TorpedoStore.class);
    this.ship = new GT4500(mockPrimaryStore, mockSecondaryStore);
  }

  @Test
  public void fireLaser_AllModes() {
	boolean result = ship.fireLaser(FiringMode.SINGLE) && ship.fireLaser(FiringMode.ALL);
	assertEquals(result, true);
  }

  @Test(expected = NullPointerException.class)
  public void fireTorpedo_InvalidMode() {
	ship.fireTorpedo(null);
  }


  @Test
  public void fireTorpedo_StartWithSecondary() {
	// Arrange
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(0);
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockPrimaryStore.fire(1)).thenThrow(IllegalArgumentException.class);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(1);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

	final GT4500 ship = new GT4500(mockPrimaryStore, mockSecondaryStore);

    // Act
	ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrimaryStore, times(0)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_Alternation() {
	final int alterCount = 100;

	// Arrange
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(alterCount);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(true);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(alterCount);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    // Act
	for (int i = 0; i < 2 * alterCount; i++) {
		ship.fireTorpedo(FiringMode.SINGLE);
	}

    // Assert
    verify(mockPrimaryStore, times(alterCount)).fire(1);
    verify(mockSecondaryStore, times(alterCount)).fire(1);
  }

  @Test
  public void fireTorpedo_NoAlternation_Success() {
	// Arrange	
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(10);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(true);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(0);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenThrow(IllegalArgumentException.class);

	// Act
	ship.fireTorpedo(FiringMode.SINGLE);
	ship.fireTorpedo(FiringMode.SINGLE);
	
	// Assert
    verify(mockPrimaryStore, times(2)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
  }

  @Test
  public void fireTorpedo_AllEmpty() {
	// Arrange	
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(0);
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockPrimaryStore.fire(1)).thenThrow(IllegalArgumentException.class);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(0);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenThrow(IllegalArgumentException.class);

	// Act
	boolean result = ship.fireTorpedo(FiringMode.SINGLE);
	
	// Assert
    verify(mockPrimaryStore, times(0)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
	assertEquals(result, false);
  }

  @Test
  public void fireTorpedo_SecondaryEmpty_PrimaryRanOut() {
	// Arrange	
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(1);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(true);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(0);
    when(mockSecondaryStore.isEmpty()).thenReturn(true);
    when(mockSecondaryStore.fire(1)).thenThrow(IllegalArgumentException.class);

	// Act
	boolean result = ship.fireTorpedo(FiringMode.SINGLE);
	
	// Primary ran out
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(0);
    when(mockPrimaryStore.isEmpty()).thenReturn(true);
    when(mockPrimaryStore.fire(1)).thenThrow(IllegalArgumentException.class);
	
	result = result && ship.fireTorpedo(FiringMode.SINGLE);
	
	// Assert
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
	assertEquals(result, false);
  }

  @Test
  public void fireTorpedo_PrimaryFailure() {
	// Arrange	
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(1);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(false);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(1);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

	// Act
	boolean result = ship.fireTorpedo(FiringMode.SINGLE);
	
	// Assert
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
	assertEquals(result, false);
  }

  @Test
  public void fireTorpedo_Primary_First() {
	// Arrange
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(10);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(true);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(10);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    // Act
    ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(0)).fire(1);
  }

  @Test
  public void fireTorpedo_All_PrimaryFailure() {
	// Arrange
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(10);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(false);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(10);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    // Act
	boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
	assertEquals(result, true);
  }

  @Test
  public void fireTorpedo_All_SecondaryFailure() {
	// Arrange
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(10);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(true);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(10);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(false);

    // Act
	boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
	assertEquals(result, true);
  }

  @Test
  public void fireTorpedo_All_AllFailure() {
	// Arrange
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(10);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(false);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(10);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(false);

    // Act
	boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
    verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
	assertEquals(result, false);
  }

  @Test
  public void fireTorpedo_Single_Success(){
    // Arrange
	when(mockPrimaryStore.getTorpedoCount()).thenReturn(10);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    verify(mockPrimaryStore, times(1)).fire(1);
  }

  @Test
  public void fireTorpedo_All_Success(){
    // Arrange
    when(mockPrimaryStore.getTorpedoCount()).thenReturn(10);
    when(mockPrimaryStore.isEmpty()).thenReturn(false);
    when(mockPrimaryStore.fire(1)).thenReturn(true);

	when(mockSecondaryStore.getTorpedoCount()).thenReturn(10);
    when(mockSecondaryStore.isEmpty()).thenReturn(false);
    when(mockSecondaryStore.fire(1)).thenReturn(true);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);

    // Assert
	verify(mockPrimaryStore, times(1)).fire(1);
    verify(mockSecondaryStore, times(1)).fire(1);
  }

}
