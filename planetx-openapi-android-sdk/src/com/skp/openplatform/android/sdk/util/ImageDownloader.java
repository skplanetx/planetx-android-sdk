package com.skp.openplatform.android.sdk.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageDownloader {
	
	public static Bitmap getImageFromUrl(String url) throws IOException
	{
		Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
		return bitmap;
	}
	

}
