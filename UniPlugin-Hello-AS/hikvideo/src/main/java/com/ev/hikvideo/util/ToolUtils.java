package com.ev.hikvideo.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ToolUtils {

	private static final double EARTH_RADIUS = 6378137.0;

	// 获取小时 yyyy-MM-dd HH:mm:ss
	public static int getHour(String date) {
		Date d = ToolUtils.parseDate(date, null);
		String hour = ToolUtils.formatDate("HH", d);
		return Integer.parseInt(hour);
	}

	// 获取分钟 yyyy-MM-dd HH:mm:ss
	public static int getMin(String date) {
		Date d = ToolUtils.parseDate(date, null);
		String mm = ToolUtils.formatDate("mm", d);
		return Integer.parseInt(mm);
	}

	// 返回单位是米
	public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(Lat1) * Math.cos(Lat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/*
	 * 旋转图片
	 *
	 * @param angle
	 *
	 * @param bitmap
	 *
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	// 获取当前版本号
	public static String getVersionCode(Context c) {
		try {
			PackageManager pakeManager = c.getPackageManager();
			PackageInfo info = pakeManager.getPackageInfo(c.getPackageName(), 0);
			return info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("获取版本号出错");
			return "无版本号";
		}
	}

	public static String filterBigFont(String body) {
		body = body.replaceAll("font-size:\\d{2}pt", "font-size:12pt");// 大字体变小
		body = body.replaceAll("font-size:\\d{2}px", "font-size:12pt");// 大字体变小
		body = body.replaceAll("font-size:\\d{2}\\.\\d{4}pt", "font-size:12pt");// 大字体变小
		body = body.replaceAll("font-size:\\d{2}\\.\\d{4}px", "font-size:12pt");// 大字体变小
		body = body.replaceAll("　", "");// 去掉全角koingge
		return body;
	}


	public static Date getNextDay() {
		return new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000));
	}


	/**
	 * 转换图片成圆形
	 *
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScreenWith(Context c) {
		WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;// 屏幕宽度（像素）
		float density = dm.density;// 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = dm.densityDpi;// 屏幕密度dpi（120 / 160 / 240）
		// 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
		int screenWidth = (int) (width / density);// 屏幕宽度(dp)
		return screenWidth;
	}

	/**
	 * 获取当前日期是星期几<br>
	 *
	 * @param dt
	 * @return 当前日期是星期几
	 */
	public static String getWeekOfDate(Date dt) {
		String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		return weekDays[w];
	}

	// 计算图片的缩放比列
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}

	// 根据路径获得图片并压缩，返回bitmap用于显示
	public static Bitmap getSmallBitmap(String filePath) {
		int degree = readPictureDegree(filePath);

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);

		// Calculate inSampleSize 1400×1050
		options.inSampleSize = calculateInSampleSize(options, 700, 525);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;

		Bitmap b = BitmapFactory.decodeFile(filePath, options);
		return rotate(b, degree);
	}

	// 读取照片偏转度
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	// 旋转到正常
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees == 0) {
			return b;
		}
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth(), (float) b.getHeight());
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle(); // Android开发网再次提示Bitmap操作完应该显示的释放
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
			}
		}
		return b;
	}

	// 保存文件
	public static boolean saveBitmap2file(Bitmap bmp, String filename) {
		CompressFormat format = Bitmap.CompressFormat.JPEG;
		int quality = 70;
		OutputStream stream = null;
		try {
			stream = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bmp.compress(format, quality, stream);
	}

	// 返回当前时间
	public static String getCurrentTime(String pattern) {
		if (pattern == null)
			pattern = "yyyy-MM-dd HH:mm:ss";

		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date());
	}

	// 返回当前时间
	public static String formatDate(String pattern, Date date) {
		if (pattern == null)
			pattern = "yyyy-MM-dd HH:mm:ss";

		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	// 返回当前时间
	public static String formatDate(String pattern, Calendar calendar) {
		if (pattern == null)
			pattern = "yyyy-MM-dd HH:mm:ss";

		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(calendar);
	}

	// 处理日期格式
	public static String parseDate(String date, String inPattern, String outPattern) {
		if (inPattern == null)
			inPattern = "yyyy-MM-dd HH:mm:ss";

		SimpleDateFormat format = new SimpleDateFormat(inPattern);
		try {
			if (date != null) {
				Date myDate = format.parse(date);
				format = new SimpleDateFormat(outPattern);
				return format.format(myDate);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return format.format(new Date());
	}

	// 处理日期格式
	public static String getDateDesc(String date, String inPattern) {
		if (inPattern == null)
			inPattern = "yyyy/MM/dd HH:mm:ss";

		SimpleDateFormat format = new SimpleDateFormat(inPattern);
		try {
			if (date != null) {
				long time1 = format.parse(date).getTime();
				long time2 = System.currentTimeMillis();

				if (time2 - time1 > 0) {
					double between_days = (double) (Math.round((time2 - time1) * 1000.0 / (1000 * 3600 * 24)) / 1000.0); // 相差天数

					if (between_days > 5) { // 大于5天返回日期
						return parseDate(date, "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd");
					}

					if (between_days >= 1 && between_days <= 8) { // 1-8天返回 几天前
						return (int) between_days + "天前";
					}

					if (between_days * 24 >= 1) { // 多少小时前
						return (int) (between_days * 24) + "小时前";
					}

					if (between_days * 24 >= 0.5 && between_days < 1) {
						return (int) (between_days * 24 * 60) + "分钟前";
					}

					if (between_days * 24 < 0.5 && between_days * 24 >= 0) {
						return "刚刚";
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return parseDate(date, "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd");
	}

	// 处理日期格式
	public static String parseDateEn(String date, String inPattern, String outPattern) {
		if (inPattern == null)
			inPattern = "yyyy-MM-dd HH:mm:ss";

		SimpleDateFormat format = new SimpleDateFormat(inPattern, Locale.ENGLISH);
		try {
			if (date != null) {
				Date myDate = format.parse(date);
				format = new SimpleDateFormat(outPattern);
				return format.format(myDate);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return format.format(new Date());
	}

	// 处理日期格式
	public static Date parseDate(String date, String inPattern) {
		if (inPattern == null)
			inPattern = "yyyy-MM-dd HH:mm:ss";

		SimpleDateFormat format = new SimpleDateFormat(inPattern);
		try {
			if (date != null) {
				Date myDate = format.parse(date);
				return myDate;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return new Date();
	}

	// 处理日期格式
	public static Date parseDate(String date) {
		try {
			long time = Long.parseLong(date);
			return new Date(time);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	/**
	 * 拷贝文件
	 *
	 * @param path
	 * @param desc
	 * @return
	 */
	public static boolean copyFile(String path, String desc) {
		try {
			FileInputStream fis = new FileInputStream(path);
			FileOutputStream fos = new FileOutputStream(desc);

			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer, 0, 1024)) != -1) {
				fos.write(buffer, 0, len);
			}

			fos.flush();
			fos.close();
			fis.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 拷贝数据
	public static void copyData(File srcFile, File descFile) {
		try {
			FileInputStream fis = new FileInputStream(srcFile);
			FileOutputStream fos = new FileOutputStream(descFile);

			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer, 0, 1024)) != -1) {
				fos.write(buffer, 0, len);
			}

			fos.flush();
			fos.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
