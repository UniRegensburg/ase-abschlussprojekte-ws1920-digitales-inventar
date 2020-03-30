package com.example.digitalesinventar;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CategoryFragmentTest {
	CategoryFragment catFrag = null;
	ArrayList <String> categoryArray;
	CategoryListAdapter catArrayAdapter = null;

	@Before
	public void setup() {
		catFrag = new CategoryFragment();
		categoryArray = DatabaseActivity.categoryArray;
		catArrayAdapter = new CategoryListAdapter(catFrag.getContext(), categoryArray);
	}

	@Test
	public void testSortByNameDown() {
		CategoryFragment.sortByNameDown();
		assertEquals(categoryArray, DatabaseActivity.categoryArray);
	}

	@Test
	public void testSortByNameUp() {

	}

	@Test
	public void testUpdateList() {

	}

	@Test
	public void testEditCategory() {

	}

	@Test
	public void testDeleteCategory() {

	}
}
