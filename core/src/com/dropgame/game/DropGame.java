package com.dropgame.game;

import java.lang.invoke.VarHandle;
import java.util.Iterator;

import javax.print.attribute.standard.MultipleDocumentHandling;
import javax.swing.AbstractSpinnerModel;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class DropGame extends ApplicationAdapter
{
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long timeLastDropCreated;
	private long dropCreationRate = 1000000000;
	
	final private int SCREEN_WIDTH = 800;
	final private int SCERRN_HEIGHT = 480;
	final private int BUCKET_WIDTH = 64;
	final private int BUCKET_HEIGHT = 64;
	
	
	@Override
	public void create () 
	{
		  camera = new OrthographicCamera();
		  camera.setToOrtho(false, 800, 480);
		  batch = new SpriteBatch();
		  bucket = new Rectangle();
		  raindrops = new Array<Rectangle>();
		  CreateADrop();
		  
		  bucket.x = (SCREEN_WIDTH - BUCKET_WIDTH) / 2; // This is the bottom right of the bucket.
		  bucket.y = 20; //This means it will be located 20 px from the bottom.
		  bucket.height = BUCKET_HEIGHT;
		  bucket.width = BUCKET_WIDTH;
		  
		  
	      // load the images for the droplet and the bucket, 64x64 pixels each
	      dropImage = new Texture(Gdx.files.internal("droplet.png"));
	      bucketImage = new Texture(Gdx.files.internal("bucket.png"));

	      // load the drop sound effect and the rain background "music"
	      dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
	      rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

	      // start the playback of the background music immediately
	      rainMusic.setLooping(true);
	      rainMusic.play();
		
	}

	@Override
	public void render ()
	{
		camera.update();
		ScreenUtils.clear(0, 0, 0.2f, 1);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) 
		{
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();
		
		moveDrops();
		
		ManageBucketMovements();
		
		CreateDropWhenNeeded();
	}

	private void moveDrops() 
	{
		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); )
		{
		      Rectangle raindrop = iter.next();
		      raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
		      if(raindrop.y + 64 < 0) iter.remove();
		      if(raindrop.overlaps(bucket))
		      {
		    	  dropSound.play();
		    	  iter.remove();
		      }
		}
	}

	@Override
	public void dispose () 
	{
		batch.dispose();;
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
	}
	
	private void CreateDropWhenNeeded() {
		if(TimeUtils.nanoTime() - timeLastDropCreated > dropCreationRate) CreateADrop();
	}

	private void ManageBucketMovements() {
		if (Gdx.input.isTouched())
		{
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - (BUCKET_WIDTH/2); 
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();
		
		if (bucket.x > SCREEN_WIDTH - BUCKET_WIDTH) bucket.x = SCREEN_WIDTH - BUCKET_WIDTH;
		if (bucket.x < 0) bucket.x = 0;
	}
	
	private void CreateADrop()
	{
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, SCREEN_WIDTH - 64);
		raindrop.y = SCERRN_HEIGHT;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		timeLastDropCreated = TimeUtils.nanoTime();
	}
	
	
}
