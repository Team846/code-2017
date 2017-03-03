package com.lynbrookrobotics.potassium.vision

import java.util.ArrayList
import scala.collection.JavaConverters._
import scala.collection.mutable.Buffer

import org.opencv.core._
import org.opencv.core.Core._
import org.opencv.features2d.FeatureDetector
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc._
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect._
import org.opencv.videoio._
import com.lynbrookrobotics.potassium._
import com.lynbrookrobotics.potassium.vision._

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import com.lynbrookrobotics.seventeen.commons._

object VisionPipeline {
  System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

  val leftCamera = new TimestampedCameraSignal(new VideoCapture(0), 1280, 720)
  val rightCamera = new TimestampedCameraSignal(new VideoCapture(1), 1280, 720)

  // TODO: implicit VisionConfiguration for thresholds
  def process(signal: TimestampedCameraSignal): Signal[VisionTargets] = {
    signal.map{ timestampedCam: TimestampedMat =>
      val cam = timestampedCam.mat

      val hsv = cam
      Imgproc.cvtColor(cam, hsv, Imgproc.COLOR_BGR2HSV);
      Core.inRange(hsv,
        new Scalar(58.2, 0.0, 0.0),
        new Scalar(76.0, 255.0, 255.0),
        hsv)
      (hsv, timestampedCam.timestamp)
    }.map{ case (hsv, timestamp) =>
      val hierarchy = new Mat()
      val contours = new ArrayList[MatOfPoint]()
      val mode = Imgproc.RETR_LIST
      val method = Imgproc.CHAIN_APPROX_SIMPLE

      Imgproc.findContours(hsv, contours, hierarchy, mode, method)
      (contours, timestamp)
    }.map{ case (contours, timestamp) =>
      (contours.asScala.filter{ contour: MatOfPoint =>
        val bb = Imgproc.boundingRect(contour)

        val hull = new MatOfInt();
        Imgproc.convexHull(contour, hull)
        val mopHull = new MatOfPoint();
        mopHull.create(hull.size.height.asInstanceOf[Int], 1, CvType.CV_32SC2)
        (0 to hull.size.height.asInstanceOf[Int]).map{ j =>
          val index = hull.get(j, 0)(0).asInstanceOf[Int]
          val point = Array(
            contour.get(index, 0)(0).asInstanceOf[Float],
            contour.get(index, 0)(1).asInstanceOf[Float]
          )
          mopHull.put(j, 0, point)
        }

        val area = Imgproc.contourArea(contour)
        val width = bb.width
        val height = bb.height
        val solidity = 100 * area / Imgproc.contourArea(mopHull);
        val vertices = contour.rows
        val ratio = bb.width / bb.height

        (area > 5000) &&
        (width < 1000) &&
        (height < 1000) &&
        (solidity > 70.1 && solidity < 100) &&
        (vertices < 1000000) &&
        (ratio < 1000)
      }.map{ contour: MatOfPoint => Imgproc.boundingRect(contour) }, timestamp)
    }.map { case (openCvRects, timestamp) =>
      VisionTargets(openCvRects.map{ rect =>
        Rectangle(rect.x, rect.y, rect.width, rect.height)
      }.asInstanceOf[List[Rectangle]], timestamp)
    }
  }

  val leftCameraProcessed = process(leftCamera)
  val rightCameraProcessed = process(rightCamera)

  def matToBufferedImage(frame: Mat, width: Int = 320, height: Int = 200, rotation: Int = 0): BufferedImage = {
    var t = 0
    if (frame.channels() == 1) {
      t = BufferedImage.TYPE_BYTE_GRAY;
    } else if (frame.channels() == 3) {
      t = BufferedImage.TYPE_3BYTE_BGR;
    }

    val image = new BufferedImage(frame.width, frame.height, t)
    val raster = image.getRaster
    val dataBuffer = raster.getDataBuffer.asInstanceOf[DataBufferByte]
    val data = dataBuffer.getData()
    frame.get(0, 0, data)
    val newHeight = (frame.height.asInstanceOf[Double] * (width.asInstanceOf[Double] / frame.width)).asInstanceOf[Int]

    val resize = image.getScaledInstance(width, newHeight, Image.SCALE_SMOOTH)
    var ret = new BufferedImage(width, newHeight, BufferedImage.TYPE_INT_RGB)

    val g2d = ret.createGraphics
    g2d.drawImage(resize, 0, 0, null)
    g2d.dispose

    if (rotation != 0) {
      val tx = new AffineTransform
      tx.rotate(Math.toRadians(rotation), ret.getWidth, ret.getHeight)

      val op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR)
      ret = op.filter(ret, null)
    }

    ret
  }
}
