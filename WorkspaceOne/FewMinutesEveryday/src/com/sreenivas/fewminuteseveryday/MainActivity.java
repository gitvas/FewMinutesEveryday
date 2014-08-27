package com.sreenivas.fewminuteseveryday;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class MainActivity extends ActionBarActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	public final static String calvinhobbesurl = "http://calvinhobbesdaily.tumblr.com/rss";
	public final static String dilberturl = "http://feed.dilbert.com/dilbert/daily_strip?format=xml";
	public final static String quotesurl = "http://feeds.feedburner.com/inspirational_quotes";
	public final static String nasaurl = "http://apod.nasa.gov/apod.rss";
	public final static String spaceurl = "http://www.space.com/home/feed/site.xml"; 
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
	}
	
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch(position){
			
			case 0:
				FragmentCalvinHobbes fch = new FragmentCalvinHobbes();
				return fch.newInstance(position + 1);
			case 1:
				return FragmentDilbert.newInstance(position + 1);
			default:
				return null;
			}
			
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class FragmentCalvinHobbes extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public FragmentCalvinHobbes newInstance(int sectionNumber) {
			FragmentCalvinHobbes fragment = new FragmentCalvinHobbes();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public FragmentCalvinHobbes() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_calvinhobbes, container,
					false);
			
			//API Call
			
			String urlstring[] = {calvinhobbesurl, "CALVINANDHOBBES"};
			new CallAPI().execute(urlstring);
			
			return rootView;
		}
	}
	
	public static class FragmentDilbert extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static FragmentDilbert newInstance(int sectionNumber) {
			FragmentDilbert fragment = new FragmentDilbert();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public FragmentDilbert() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_dilbert, container,
					false);
			return rootView;
		}
	}
	
	//Invoking Async Rest API call
	
	private class CallAPI extends AsyncTask<String, ActionBarActivity, ResultParse> {
		 		
	    @Override
	    protected ResultParse doInBackground(String... params) {
	 
	    	 String urlString=params[0]; // URL to call
	    	 
	    	 ResultParse result = null;  
	         InputStream in = null;
	   
	         // HTTP Get
	         try {
	   
	             URL url = new URL(urlString);
	   
	             HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	   
	             in = new BufferedInputStream(urlConnection.getInputStream());
	   
	          } catch (Exception e ) {
	   
	             System.out.println(e.getMessage());
	   
	             return null;
	   
	          } 
	         
	         // Parse XML
	         XmlPullParserFactory pullParserFactory;
	      
	         try {
	           pullParserFactory = XmlPullParserFactory.newInstance();
	           XmlPullParser parser = pullParserFactory.newPullParser();
	      
	           parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	           parser.setInput(in, null);
	           result = parseXML(parser, params[1]);
	         } catch (XmlPullParserException e) {
	           e.printStackTrace();
	         } catch (IOException e) {
	           e.printStackTrace();
	         }
	   
	         return result; 
	 
	    }
	 
	    protected void onPostExecute(ResultParse result) {
	    	String image;
	    	int loader = R.drawable.calvin_hobbes;
	    	Pattern p = Pattern.compile("http[^\"]*");
	    	Matcher m = p.matcher(result.imageURL);
	    	if(m.find())
	    	{
	    		image = m.group();
	    		ImageView imageview = (ImageView) findViewById(R.id.imageView1);
	    		ImageLoader imgLoader = new ImageLoader(getApplicationContext());
	    		imgLoader.DisplayImage(image, loader, imageview);
	    			    		
	    	}
	    		
	    }
	    
	} // end CallAPI 
	
	//XML Parser
	
	private static ResultParse parseXML( XmlPullParser parser , String subject) throws XmlPullParserException, IOException {
		 
	    int eventType = parser.getEventType();
	    ResultParse result = null;
	    boolean endparse = true;
	    String text = null;
	 
	    while( (eventType!= XmlPullParser.END_DOCUMENT) && endparse ) {
	      String name = null;
	 
	      switch(eventType)
	      {
	        case XmlPullParser.START_TAG:
	        name = parser.getName();
	          
	          if ( name.equals("item")) {
	        	  result = new ResultParse();
	          }
	          break;
	          
	        case XmlPullParser.TEXT:
	        	text = parser.getText();
	        	break;
	        	
	        case XmlPullParser.END_TAG:
	        	name = parser.getName();
	        	if((name.equalsIgnoreCase("description")) && (result!=null))
	        	{
	        		result.imageURL = text;
	        		result.subject = null;
	        		endparse = false;
	        	}
	          break;
	       } // end switch
	 
	       eventType = parser.next();
	    } // end while
	    
	    return result;       
	}
	
	//Response parsing
	
	private static class ResultParse {
		
		public String imageURL;
		public String subject;
	}
	
	public class ImageLoader {
		  
	    MemoryCache memoryCache=new MemoryCache();
	    FileCache fileCache;
	    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	    ExecutorService executorService; 
	  
	    public ImageLoader(Context context){
	        fileCache=new FileCache(context);
	        executorService=Executors.newFixedThreadPool(5);
	    }
	  
	    int stub_id = R.drawable.ic_launcher;
	    public void DisplayImage(String url, int loader, ImageView imageView)
	    {
	        stub_id = loader;
	        imageViews.put(imageView, url);
	        Bitmap bitmap=memoryCache.get(url);
	        if(bitmap!=null)
	            imageView.setImageBitmap(bitmap);
	        else
	        {
	            queuePhoto(url, imageView);
	            imageView.setImageResource(loader);
	        }
	    }
	  
	    private void queuePhoto(String url, ImageView imageView)
	    {
	        PhotoToLoad p=new PhotoToLoad(url, imageView);
	        executorService.submit(new PhotosLoader(p));
	    }
	  
	    private Bitmap getBitmap(String url)
	    {
	        File f=fileCache.getFile(url);
	  
	        //from SD cache
	        Bitmap b = decodeFile(f);
	        if(b!=null)
	            return b;
	  
	        //from web
	        try {
	            Bitmap bitmap=null;
	            URL imageUrl = new URL(url);
	            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
	            conn.setConnectTimeout(30000);
	            conn.setReadTimeout(30000);
	            conn.setInstanceFollowRedirects(true);
	            InputStream is=conn.getInputStream();
	            OutputStream os = new FileOutputStream(f);
	            Utils.CopyStream(is, os);
	            os.close();
	            bitmap = decodeFile(f);
	            return bitmap;
	        } catch (Exception ex){
	           ex.printStackTrace();
	           return null;
	        }
	    }
	  
	    //decodes image and scales it to reduce memory consumption
	    private Bitmap decodeFile(File f){
	        try {
	            //decode image size
	            BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
	  
	            //Find the correct scale value. It should be the power of 2.
	            final int REQUIRED_SIZE=70;
	            int width_tmp=o.outWidth, height_tmp=o.outHeight;
	            int scale=1;
	            while(true){
	                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                    break;
	                width_tmp/=2;
	                height_tmp/=2;
	                scale*=2;
	            }
	  
	            //decode with inSampleSize
	            BitmapFactory.Options o2 = new BitmapFactory.Options();
	            o2.inSampleSize=scale;
	            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	        } catch (FileNotFoundException e) {}
	        return null;
	    }
	  
	    //Task for the queue
	    private class PhotoToLoad
	    {
	        public String url;
	        public ImageView imageView;
	        public PhotoToLoad(String u, ImageView i){
	            url=u;
	            imageView=i;
	        }
	    }
	  
	    class PhotosLoader implements Runnable {
	        PhotoToLoad photoToLoad;
	        PhotosLoader(PhotoToLoad photoToLoad){
	            this.photoToLoad=photoToLoad;
	        }
	  
	        @Override
	        public void run() {
	            if(imageViewReused(photoToLoad))
	                return;
	            Bitmap bmp=getBitmap(photoToLoad.url);
	            memoryCache.put(photoToLoad.url, bmp);
	            if(imageViewReused(photoToLoad))
	                return;
	            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
	            ActionBarActivity a=(ActionBarActivity)photoToLoad.imageView.getContext();
	            a.runOnUiThread(bd);
	        }
	    }
	  
	    boolean imageViewReused(PhotoToLoad photoToLoad){
	        String tag=imageViews.get(photoToLoad.imageView);
	        if(tag==null || !tag.equals(photoToLoad.url))
	            return true;
	        return false;
	    }
	  
	    //Used to display bitmap in the UI thread
	    class BitmapDisplayer implements Runnable
	    {
	        Bitmap bitmap;
	        PhotoToLoad photoToLoad;
	        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
	        public void run()
	        {
	            if(imageViewReused(photoToLoad))
	                return;
	            if(bitmap!=null)
	                photoToLoad.imageView.setImageBitmap(bitmap);
	            else
	                photoToLoad.imageView.setImageResource(stub_id);
	        }
	    }
	  
	    public void clearCache() {
	        memoryCache.clear();
	        fileCache.clear();
	    }
	  
	}
	
	public class FileCache {
		  
	    private File cacheDir;
	  
	    public FileCache(Context context){
	        //Find the dir to save cached images
	        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
	            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"TempImages");
	        else
	            cacheDir=context.getCacheDir();
	        if(!cacheDir.exists())
	            cacheDir.mkdirs();
	    }
	  
	    public File getFile(String url){
	        String filename=String.valueOf(url.hashCode());
	        File f = new File(cacheDir, filename);
	        return f;
	  
	    }
	  
	    public void clear(){
	        File[] files=cacheDir.listFiles();
	        if(files==null)
	            return;
	        for(File f:files)
	            f.delete();
	    }
	  
	}
	
	public class MemoryCache {
	    private Map<String, SoftReference<Bitmap>> cache=Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());
	  
	    public Bitmap get(String id){
	        if(!cache.containsKey(id))
	            return null;
	        SoftReference<Bitmap> ref=cache.get(id);
	        return ref.get();
	    }
	  
	    public void put(String id, Bitmap bitmap){
	        cache.put(id, new SoftReference<Bitmap>(bitmap));
	    }
	  
	    public void clear() {
	        cache.clear();
	    }
	}
	
	public static class Utils {
	    public static void CopyStream(InputStream is, OutputStream os)
	    {
	        final int buffer_size=1024;
	        try
	        {
	            byte[] bytes=new byte[buffer_size];
	            for(;;)
	            {
	              int count=is.read(bytes, 0, buffer_size);
	              if(count==-1)
	                  break;
	              os.write(bytes, 0, count);
	            }
	        }
	        catch(Exception ex){}
	    }
	}
	
}

