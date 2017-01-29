package josiah.test;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import battlecode.common.RobotController;
import josiah_boid_garden.util.GlobalMap;
import josiah_boid_garden.util.MapArrayLocal;


public class MapTests {

	float xOffset;
	float yOffset;
	float width;
	float height;
	
	GlobalMap map;
	
	@Before
	public void setUp(){
		xOffset=100;
		yOffset=100;
		width = 80;
		height = 40;
		map = new GlobalMap( new MapArrayLocal());
		map.setEastBound(xOffset + width);
		map.setWestBound(xOffset);
		map.setSouthBound(yOffset);
		map.setNorthBound(yOffset + height);	 
		map.check();
	}
	
	@Test
	public void eastBoundsTest() {
		map.setEastBound(xOffset);
		assertTrue("Issue with setting bounds",map.hasEastBound());
		
	}
	
	@Test
	public void westBoundsTest() {
	
		assertTrue("Issue with setting bounds",map.hasWestBound());
		
	}
	
	@Test
	public void southBoundsTest() {
	
		assertTrue("Issue with setting bounds",map.hasSouthBound());
		
	}
	
	@Test
	public void northBoundsTest() {
	
		assertTrue("Issue with setting bounds",map.hasNorthBound());
		
	}
	
	@Test
	public void boundsTest() {
	
		assertTrue("Issue with setting bounds",map.check());
		
	}
	
	@Test
	public void widthTest() {
	
		assertEquals("Issue with setting bounds",width,map.getWidth(),0.01);
		
	}
	
	@Test
	public void heightTest() {
		assertEquals("Issue with setting bounds",height,map.getHeight(),0.01);
	}
	
	@Test
	public void offsetTest() {
		assertEquals("offset problem",yOffset,map.getYOffset(),0.01);
		assertEquals("offset problem",xOffset,map.getXOffset(),0.01);
	}
	
	@Test
	public void discreteCoordConversionTest(){
		
		Assert.assertEquals((long)(height/4), map.getYDivisions());
		Assert.assertEquals((long)(width/4), map.getXDivisions());
		//0,0 tests
		Assert.assertEquals( " x coord to discrete map issue", 0 , map.getXIndex(101) );
		Assert.assertEquals( " y coord to discrete map issue", 0 , map.getYIndex(101) );

		Assert.assertEquals( " x coord to discrete map issue", 0 , map.getXIndex(101) );
		Assert.assertEquals( " y coord to discrete map issue", 0 , map.getYIndex(103) );

		Assert.assertEquals( " x coord to discrete map issue", 0 , map.getXIndex(103) );
		Assert.assertEquals( " y coord to discrete map issue", 0 , map.getYIndex(103) );

		Assert.assertEquals( " x coord to discrete map issue", 0 , map.getXIndex(103.9f) );
		Assert.assertEquals( " y coord to discrete map issue", 0 , map.getYIndex(101) );

		// 3,3 tests
		Assert.assertEquals( " x coord to discrete map issue", 3 , map.getXIndex(112.9f) );
		Assert.assertEquals( " y coord to discrete map issue", 3 , map.getYIndex(114) );

		Assert.assertEquals( " x coord to discrete map issue", 3 , map.getXIndex(112f) );
		Assert.assertEquals( " y coord to discrete map issue", 3 , map.getYIndex(115.9f) );

		Assert.assertEquals( " x coord to discrete map issue", 3 , map.getXIndex(112f) );
		Assert.assertEquals( " y coord to discrete map issue", 3 , map.getYIndex(115.9f) );
	
	}
	
	@Test
	public void arrayIndexTest(){
		//ensure 10 y divisions (not that it matters)
		//ensure 20 x divisions (this one matters)
		Assert.assertEquals((long)(10), map.getYDivisions());
		Assert.assertEquals((long)(20), map.getXDivisions());
		//0,0 test
		Assert.assertEquals( "issue with finding array index", 0 ,map.getArrayIndexFromMapIndex(0,0));
		//3,3 test
		Assert.assertEquals( "issue with finding array index", 63 ,map.getArrayIndexFromMapIndex(3,3));
	}
	
	@Test
	public void arrayIndexToChannelTest(){
		
		
		
	}
}
