package com.lynbrookrobotics.seventeen.commons

case class Point(x: Double, y: Double)
case class Rectangle(x: Double, y: Double, width: Double, height: Double)

case class VisionTargets(targets: List[Rectangle])
case class ContourRender(contours: List[List[Point]])
