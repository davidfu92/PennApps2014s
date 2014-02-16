
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;


public class Main extends SimpleApplication
        implements ActionListener{

  private Spatial levelOne;
  private Spatial levelTwo;
  private BulletAppState bulletAppState;
  private RigidBodyControl landscape;
  private CharacterControl player;
  private Vector3f walkDirection = new Vector3f();
  private boolean left = false, right = false, up = false, down = false, open = false;
  

  public static void main(String[] args) {
    Main app = new Main();
     //some presets
    AppSettings settings = new AppSettings(true);
    settings.setResolution(800,600);
    settings.setFrameRate(60);
    settings.setTitle("Simulation");
    //settings.setFullscreen(true);
    app.setSettings(settings);
    //app.setShowSettings(false);
    app.setPauseOnLostFocus(true);
    app.setShowSettings(false);
    app.start();
  }

  public void simpleInitApp() {   
    //don't show stats
    setDisplayFps(false);
    setDisplayStatView(false);
    /** Set up Physics */
    bulletAppState = new BulletAppState();
    bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);//for bullets
    stateManager.attach(bulletAppState);
    //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
    // We re-use the flyby camera for rotation, while positioning is handled by physics
    viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
    flyCam.setMoveSpeed(100);
    setUpKeys();
    setUpLight();
    //setUpSky();
    //Preload Levels
    levelOne = assetManager.loadModel("Scenes/newScene.j3o");
    levelOne.setLocalScale(5f);
    levelTwo = assetManager.loadModel("Scenes/newScene1.j3o");
    levelTwo.setLocalScale(5f);
    //Set up physics and add level/player to scenegraph
    CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
    player = new CharacterControl(capsuleShape, 0.05f);
    player.setPhysicsLocation(new Vector3f(0, 14, 120));
    setLevel(levelOne,player);
  }
  
  private void setLevel(Spatial level, CharacterControl player) {
      // We set up collision detection for the scene by creating a
    // compound collision shape and a static RigidBodyControl with mass zero.
      CollisionShape sceneShape =
            CollisionShapeFactory.createMeshShape((Node) level);
    landscape = new RigidBodyControl(sceneShape, 0);
    if(level.getNumControls()>=1) {
        bulletAppState = new BulletAppState();
        level.removeControl(level.getControl(0));
    }
    
    level.addControl(landscape);

    // We set up collision detection for the player by creating
    // a capsule collision shape and a CharacterControl.
    // The CharacterControl offers extra settings for
    // size, stepheight, jumping, falling, and gravity.
    // We also put the player in its starting position.
    player.setJumpSpeed(20);
    player.setFallSpeed(30);
    player.setGravity(30);
    player.setPhysicsLocation(player.getPhysicsLocation());
    // We attach the scene and the player to the rootnode and the physics space,
    // to make them appear in the game world.
    rootNode.detachAllChildren();
    rootNode.attachChild(level);
    
    bulletAppState.getPhysicsSpace().add(landscape);
    bulletAppState.getPhysicsSpace().add(player); 
  }

  private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
  }
  
  private void setUpLight() {
    // We add light so we see the scene
    AmbientLight al = new AmbientLight();
    al.setColor(ColorRGBA.White.mult(3.3f));
    rootNode.addLight(al);

    DirectionalLight dl = new DirectionalLight();
    dl.setColor(ColorRGBA.White);
    dl.setDirection(new Vector3f(1.5f, -2, 2).normalizeLocal());
    rootNode.addLight(dl);
  }

  /** We over-write some navigational key mappings here, so we can
   * add physics-controlled walking and jumping: */
  private void setUpKeys() {
    inputManager.deleteMapping(INPUT_MAPPING_EXIT);
    inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("Quit", new KeyTrigger(KeyInput.KEY_ESCAPE));
    inputManager.addMapping("Open", new KeyTrigger(KeyInput.KEY_8));
    inputManager.addListener(this, "Left");
    inputManager.addListener(this, "Right");
    inputManager.addListener(this, "Up");
    inputManager.addListener(this, "Down");
    inputManager.addListener(this, "Jump");
    inputManager.addListener(this, "Open");
    inputManager.addListener(this, "Quit"); // listen for ESC key to close game
  }
  
  /*private void setUpSky() {
  
        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/down.jpg");

        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
        rootNode.attachChild(sky);
  
  }*/

  /** These are our custom actions triggered by key presses.
   * We do not walk yet, we just keep track of the direction the user pressed. */
  public void onAction(String binding, boolean value, float tpf) {
    if (binding.equals("Left")) {
      if (value) { left = true; } else { left = false; }
    } else if (binding.equals("Right")) {
      if (value) { right = true; } else { right = false; }
    } else if (binding.equals("Up")) {
      if (value) { up = true; } else { up = false; }
    } else if (binding.equals("Down")) {
      if (value) { down = true; } else { down = false; }
    } else if (binding.equals("Jump")) {
      player.jump();
    } else if (binding.equals("Open")) {
      if (value) { open = true; } else { open = false; }
    }  else if (binding.equals("Quit")) {
      stop(); // simple way to close game
    } 
  }

  
  /**
   * This is the main event loop--walking happens here.
   * We check in which direction the player is walking by interpreting
   * the camera direction forward (camDir) and to the side (camLeft).
   * The setWalkDirection() command is what lets a physics-controlled player walk.
   * We also make sure here that the camera moves with player.
   */
  @Override
  public void simpleUpdate(float tpf) {
    
        Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
        Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left)  { walkDirection.addLocal(camLeft); }
        if (right) { walkDirection.addLocal(camLeft.negate()); }
        if (up)    { walkDirection.addLocal(camDir); }
        if (down)  { walkDirection.addLocal(camDir.negate()); }
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());  
        if (open)  {setLevel(levelTwo, player); }
  }
}