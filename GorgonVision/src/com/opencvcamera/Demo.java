package com.opencvcamera;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

public class Demo extends JFrame {

	private JLabel label;
	private ImageIcon icon;
	private VideoCapture capture;
	private Mat image;

	private boolean closed = false;

	public Demo() {
		setLayout(null);

		label = new JLabel();
		label.setBounds(0, 0, 640, 480);
		add(label);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosed(e);
				capture.release();
				image.release();
				closed = true;
				System.out.println("closed");
				System.exit(0);

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				super.windowDeactivated(e);
				System.out.println("closed");
			}

		});

		setFocusable(false);
		setSize(654, 515);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public static void main(String[] args) {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Demo d = new Demo();
				new Thread(new Runnable() {
					public void run() {
						d.startCamera();
					}
				}).start();
			}
		});

	}

	public void startCamera() {

		CascadeClassifier faceDetector = new CascadeClassifier();
		faceDetector.load("haarcascade_eye.xml");

		capture = new VideoCapture(0);
		image = new Mat();
		Mat blurred = new Mat();

		byte[] imageData;
		while (true) {
			capture.read(image);
			MatOfRect faceDetections = new MatOfRect();
			faceDetector.detectMultiScale(image, faceDetections);

			// Creating a rectangular box which represents for
			// faces detected
			if (faceDetections.toArray().length > 0) {
				Imgproc.blur(image, blurred, new Size(20, 20));
				for (Rect rect : faceDetections.toArray()) {
					Imgproc.circle(blurred, new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), 10,
							new Scalar(0, 0, 255), -1);
				}
				final MatOfByte buf = new MatOfByte();
				Imgcodecs.imencode(".jpg", blurred, buf);
				imageData = buf.toArray();

			} else {
				final MatOfByte buf = new MatOfByte();
				Imgcodecs.imencode(".jpg", image, buf);
				imageData = buf.toArray();
			}

			icon = new ImageIcon(imageData);
			label.setIcon(icon);
			System.out.println(image.cols());

			if (closed) {
				break;
			}
		}
	}

}