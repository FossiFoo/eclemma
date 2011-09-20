/*******************************************************************************
 * Copyright (c) 2006, 2011 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 ******************************************************************************/
package com.mountainminds.eclemma.internal.core.instr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mountainminds.eclemma.core.ISourceLocation;
import com.mountainminds.eclemma.core.JavaProjectKit;

/**
 * Tests for {@link ClassFiles}.
 */
public class ClassFilesTest {

  private JavaProjectKit javaProject;

  private IPackageFragmentRoot rootSrc1;

  private IPackageFragmentRoot rootSrc2;

  private IPackageFragmentRoot rootBin;

  @Before
  public void setup() throws Exception {
    javaProject = new JavaProjectKit();
    rootSrc1 = javaProject.createSourceFolder("src1");
    rootSrc2 = javaProject.createSourceFolder("src2");
    rootBin = javaProject.createJAR("testdata/bin/signatureresolver.jar",
        "/sample.jar", new Path("/UnitTestProject/sample.jar"), null);
    JavaProjectKit.waitForBuild();
  }

  @After
  public void teardown() throws Exception {
    javaProject.destroy();
  }

  @Test
  public void testInitSrc() throws JavaModelException {
    IPath location = new Path("bin");
    ClassFiles classFiles = new ClassFiles(rootSrc1, location);
    assertEquals(location, classFiles.getLocation());
    assertFalse(classFiles.isBinary());
    IPackageFragmentRoot[] roots = classFiles.getPackageFragmentRoots();
    assertEquals(1, roots.length);
    assertEquals(rootSrc1, roots[0]);
  }

  @Test
  public void testInitBin() throws JavaModelException {
    IPath location = new Path("/sample.jar");
    ClassFiles classFiles = new ClassFiles(rootBin, location);
    assertEquals(location, classFiles.getLocation());
    assertTrue(classFiles.isBinary());
    IPackageFragmentRoot[] roots = classFiles.getPackageFragmentRoots();
    assertEquals(1, roots.length);
    assertEquals(rootBin, roots[0]);
  }

  @Test
  public void testAddRoot1() throws JavaModelException {
    IPath location = new Path("bin");
    ClassFiles classFiles = new ClassFiles(rootSrc1, location);
    classFiles = classFiles.addRoot(rootSrc2);
    assertEquals(location, classFiles.getLocation());
    assertFalse(classFiles.isBinary());
    IPackageFragmentRoot[] roots = classFiles.getPackageFragmentRoots();
    assertEquals(2, roots.length);
    assertEquals(rootSrc1, roots[0]);
    assertEquals(rootSrc2, roots[1]);
  }

  @Test
  public void testAddRoot2() throws JavaModelException {
    IPath location = new Path("bin");
    ClassFiles classFiles = new ClassFiles(rootSrc1, location);
    classFiles = classFiles.addRoot(rootBin);
    assertEquals(location, classFiles.getLocation());
    assertFalse(classFiles.isBinary());
    IPackageFragmentRoot[] roots = classFiles.getPackageFragmentRoots();
    assertEquals(2, roots.length);
    assertEquals(rootSrc1, roots[0]);
    assertEquals(rootBin, roots[1]);
  }

  @Test
  public void testAddRoot3() throws JavaModelException {
    IPath location = new Path("bin");
    ClassFiles classFiles = new ClassFiles(rootBin, location);
    classFiles = classFiles.addRoot(rootBin);
    assertEquals(location, classFiles.getLocation());
    assertTrue(classFiles.isBinary());
    IPackageFragmentRoot[] roots = classFiles.getPackageFragmentRoots();
    assertEquals(2, roots.length);
    assertEquals(rootBin, roots[0]);
    assertEquals(rootBin, roots[1]);
  }

  @Test
  public void testGetSourceLocations_source() throws JavaModelException {
    ClassFiles classFiles = new ClassFiles(rootSrc1, new Path("bin"))
        .addRoot(rootSrc2);
    ISourceLocation[] sourceLocations = classFiles.getSourceLocations();
    assertEquals(2, sourceLocations.length);

    String expected = "/UnitTestProject/src1";
    String actual = sourceLocations[0].getPath().toString();
    assertTrue(actual, actual.endsWith(expected));

    expected = "/UnitTestProject/src2";
    actual = sourceLocations[1].getPath().toString();
    assertTrue(actual, actual.endsWith(expected));
  }

  @Test
  public void testGetSourceLocations_binary() throws JavaModelException {
    ClassFiles classFiles = new ClassFiles(rootBin, new Path("bin"));
    ISourceLocation[] sourceLocations = classFiles.getSourceLocations();
    assertEquals(1, sourceLocations.length);
    String expected = "/UnitTestProject/sample.jar";
    String actual = sourceLocations[0].getPath().toString();
    assertTrue(actual, actual.endsWith(expected));
  }

}
