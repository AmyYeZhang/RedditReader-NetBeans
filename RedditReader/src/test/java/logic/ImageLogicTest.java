package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Board;
import entity.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import junit.framework.TestCase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


 
/**
 * Test the functions in ImageLogic class.
 * @author Ziyue Wang 040919399
 * @author Ye Zhang   040958453
 * 
 */

public class ImageLogicTest extends TestCase {
    
    /** The b logic. */
    private BoardLogic bLogic ;
    
    /** The h logic. */
    private HostLogic hLogic ;
    
    /** The board. */
    private Board board;
    
    /** The i logic. */
    private ImageLogic iLogic;
    
    /** The expected image. */
    private Image expectedImage;
    
    /** The expected images list. */
    private List<Image> expectedImagesList;
    
    /** The board URL. */
    private  String boardURL ;
    
    /** The board name. */
    private  String boardName ;
    
    /** The date. */
    private  Date date;
  
    /**
    * Sets the up before class.
    *
    * @throws Exception the exception
    */
   @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat("/RedditReader", "common.ServletListener");
    }

    /**
     * Tear down after class.
     *
     * @throws Exception the exception
     */
    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }
    
    /**
     * Sets up before each test.
     * It initializes an Image and imageList as test objects. 
     * Since the foreign key of Image is the primary key of board,
     *  it also sets the URL,name and foreign key of board to get the correct board.
     *  it uses merge to create an expected image object to compare with an actual image.
     * @throws Exception the exception
     */
    @BeforeEach
    protected void setUp() throws Exception {
        bLogic = LogicFactory.getFor("Board");
        hLogic = LogicFactory.getFor("Host");
        boardURL = "JunitTestBoardURL";
        boardName = "JunitBoardName";
        date = new Date(0);
        int hostId = 1;
        board = new Board();
        board.setHostid(hLogic.getWithId(hostId));
        board.setUrl(boardURL);
        board.setName(boardName);
        bLogic.add(board);
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        Image image = new Image();
        image.setBoard(bLogic.getBoardWithUrl(boardURL));
        image.setDate(date);
        image.setUrl("testUrl");
        image.setTitle("JUnit");
        image.setLocalPath("localPath");
        
        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedImage = em.merge(image);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
       // em.close();

        iLogic = LogicFactory.getFor("Image");
        expectedImagesList = new ArrayList<>();
        expectedImagesList.add(expectedImage);
    }
    
    /**
     * 
     * if expectedImage is not null, it deletes the expected image and board in database
     * after each test so there is no duplicate entry for next test.
     *
     * @throws Exception the exception
     */
    @AfterEach
    protected void tearDown() throws Exception {
         if (expectedImage != null) {
            iLogic.delete(expectedImage);
            bLogic.delete(board);
        }
    }

    
    /**
     * Test get all method.
     * it gets all images from the DB.
     * it tests image was created successfully by using assertNotNull(),
     * it tests image was created successfully after deleting an element by comparing the sizes.
     */
   
    @Test
    final void testGetAll() {
        //get all the accounts from the DB
        List<Image> list = iLogic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure image was created successfully
        assertNotNull(expectedImage);
        //delete the new image
        iLogic.delete(expectedImage);

        //get all image again
        list = iLogic.getAll();
        //the new size of accounts must be one less
        assertEquals(originalSize - 1, list.size());
    }
    
    /**
     * helper method for testing all account fields.
     *
     * @param expected the expected
     * @param actual the actual
     */
    private void assertImageEquals(Image expected, Image actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getBoard(), actual.getBoard());
        assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getLocalPath(), actual.getLocalPath());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getUrl(), actual.getUrl());
        
    }
     
     /**
      * Test get with id.
      * it uses the id of test image get another image from logic,
      * the two images (testImages and returnedimages) must be the same
      */
     @Test
    final void testGetWithId() {
       
        Image returnedImage = iLogic.getWithId(expectedImage.getId());
        assertImageEquals(expectedImage, returnedImage);
    }
    
     /**
      * Test get images with board id.
      * it uses the board id of test image get another image from logic,
      * the two image lists (expectedImages and returnedimages) must be the same
      */
     @Test
    final void testGetImagesWithBoardId() {
      List<Image> returnedImages = new ArrayList();
      for(int i = 0; i< expectedImagesList.size();i++){
          Image returnedImage = iLogic.getWithId(expectedImagesList.get(i).getId());
         returnedImages.add(returnedImage);
      }
        assertEquals(expectedImagesList,returnedImages);
    }

     

     /**
      * Test get images with title.
      * it uses the title of test image get another image from logic,
      * the two image lists (expectedImages and returnedimages) must be the same
      */
    @Test
    final void testGetImageWithTitle(){
        List<Image> returnedImages = iLogic.getImagesWithTitle(expectedImage.getTitle());
        assertEquals(expectedImagesList,returnedImages);
       
      }
       
    

    /**
     * Test get images with board url.
     * it uses the url of test image get another image from logic,
     * the two images (expectedImages and returnedimages) must be the same
     */
    @Test
    final void testGetImageWithUrl(){
        Image returnedImage = iLogic.getImageWithUrl(expectedImage.getUrl());
        assertImageEquals(expectedImage, returnedImage);
                
    }
    

    /**
     * Test get images with local path.
     * it uses the local path of test image get another image from logic,
     * the two image lists (expectedImages and returnedimages) must be the same
     */
    @Test
    final void testGetImageWithLocalPath(){
        Image returnedImage = iLogic.getImageWithLocalPath(expectedImage.getLocalPath());
        assertImageEquals(expectedImage, returnedImage);
    }
    
    /**
     * Test get images with date.
     * it uses the date of test image get another image from logic,
     * the two image lists (expectedImages and returnedimages) must be the same
     */
    @Test 
     final void testGetImagesWithDate(){
        List<Image> returnedImageList = iLogic.getImagesWithDate(expectedImage.getDate());
        assertEquals(expectedImagesList, returnedImageList);
    }
    
    /**
     * Test convert date.
     * it uses convertDate to convert the date of expectedImage form logic
     * it also converts the date of expectedImage by using simpleDateFormat
     * the two string must be the same
     */
    @Test
    final void testConvertDate(){
        String returnedDate = iLogic.convertDate(expectedImage.getDate());
        String expectedDate = ImageLogic.FORMATTER.format(expectedImage.getDate());
        assertTrue(returnedDate.equals(expectedDate));
    }
    
    /**
     * Test create entity.
     * it uses createEntiy of test image create another image from logic,
     * the two image entity (expectedImage and returnedimages) must be the same
     */
    @Test
    final void testCreateEntity(){
        Map<String, String[]> imageMap = new HashMap<>();
        imageMap.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
        //imageMap.put(ImageLogic.BOARD_ID, new String[]{expectedImage.getBoard().getId()});
        imageMap.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
        imageMap.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
        imageMap.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
        String id = Integer.toString(expectedImage.getBoard().getId());
        imageMap.put(ImageLogic.BOARD_ID, new String[]{id});
        imageMap.put(ImageLogic.DATE, new String[]{ImageLogic.FORMATTER.format(expectedImage.getDate())});
        
        Image returnedImage= iLogic.createEntity(imageMap);
        //returnedImage.setBoard(bLogic.getBoardsWithUrl(boardURL));
        returnedImage.setDate(date);
        assertImageEquals(expectedImage, returnedImage);
    }
    
    /**
     * Test create entity null and empty values.
     * it put null and empty values in the imageMap
     * it should throw null pointer exception and IndexOutOfBoundsException
     */
    @Test
    final void testCreateEntityNullAndEmptyValues(){
    Map<String, String[]> imageMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
            map.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
            map.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
            map.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
            String id = Integer.toString(expectedImage.getBoard().getId());
            map.put(ImageLogic.BOARD_ID, new String[]{id});
            map.put(ImageLogic.DATE, new String[]{ImageLogic.FORMATTER.format(expectedImage.getDate())});
            
        };
         fillMap.accept(imageMap);
        imageMap.replace(ImageLogic.ID, null);
        assertThrows(NullPointerException.class, () -> iLogic.createEntity(imageMap));
        imageMap.replace(ImageLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> iLogic.createEntity(imageMap));

        fillMap.accept(imageMap);
        imageMap.replace(ImageLogic.URL, null);
        assertThrows(NullPointerException.class, () -> iLogic.createEntity(imageMap));
        imageMap.replace(ImageLogic.URL, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> iLogic.createEntity(imageMap));

        fillMap.accept(imageMap);
        imageMap.replace(ImageLogic.LOCAL_PATH, null);
        assertThrows(NullPointerException.class, () -> iLogic.createEntity(imageMap));
        imageMap.replace(ImageLogic.LOCAL_PATH, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> iLogic.createEntity(imageMap));

        fillMap.accept(imageMap);
        imageMap.replace(ImageLogic.TITLE, null);
        assertThrows(NullPointerException.class, () -> iLogic.createEntity(imageMap));
        imageMap.replace(ImageLogic.TITLE, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> iLogic.createEntity(imageMap));
    }
    
    /**
     * Test create entity bad length values.
     * it put bad length and value in the imageMap
     * it should throw  ValidationException
     */
    @Test
    final void testCreateEntityBadLengthValues(){
    Map<String, String[]> imageMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();
            map.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
            map.put(ImageLogic.LOCAL_PATH, new String[]{expectedImage.getLocalPath()});
            map.put(ImageLogic.TITLE, new String[]{expectedImage.getTitle()});
            map.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});
            String id = Integer.toString(expectedImage.getBoard().getId());
            map.put(ImageLogic.BOARD_ID, new String[]{id});
           // map.put(ImageLogic.DATE, new String[]{ImageLogic.FORMATTER.format(expectedImage.getDate())});
            map.put(ImageLogic.DATE, new String[]{ImageLogic.FORMATTER.format(expectedImage.getDate())});
        };

        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept(imageMap);
        imageMap.replace(ImageLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> iLogic.createEntity(imageMap));
        imageMap.replace(ImageLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> iLogic.createEntity(imageMap));

        fillMap.accept(imageMap);
        imageMap.replace(ImageLogic.LOCAL_PATH, new String[]{""});
        assertThrows(ValidationException.class, () -> iLogic.createEntity(imageMap));
        imageMap.replace(ImageLogic.LOCAL_PATH, new String[]{generateString.apply(2000)});
        assertThrows(ValidationException.class, () -> iLogic.createEntity(imageMap));

        fillMap.accept(imageMap);
        imageMap.replace(ImageLogic.TITLE, new String[]{""});
        assertThrows(ValidationException.class, () -> iLogic.createEntity(imageMap));
        imageMap.replace(ImageLogic.TITLE, new String[]{generateString.apply(2000)});
        assertThrows(ValidationException.class, () -> iLogic.createEntity(imageMap));

        fillMap.accept(imageMap);
        imageMap.replace(ImageLogic.URL, new String[]{""});
        assertThrows(ValidationException.class, () -> iLogic.createEntity(imageMap));
        imageMap.replace(ImageLogic.URL, new String[]{generateString.apply(2000)});
        assertThrows(ValidationException.class, () -> iLogic.createEntity(imageMap));
        
    }
    
     /**
      * Test create entity edge values.
      * it put edge value in the imageMap
      * it should equal expected image.
      */
     @Test
    final void testCreateEntityEdgeValues() {
       IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        Map<String, String[]> imageMap = new HashMap<>();
        imageMap.put(ImageLogic.ID, new String[]{Integer.toString(1)});
        imageMap.put(ImageLogic.TITLE, new String[]{generateString.apply(1)});
        imageMap.put(ImageLogic.URL, new String[]{generateString.apply(1)});
        imageMap.put(ImageLogic.LOCAL_PATH, new String[]{generateString.apply(1)});
         String id = Integer.toString(expectedImage.getBoard().getId());
        imageMap.put(ImageLogic.BOARD_ID, new String[]{id});
        imageMap.put(ImageLogic.DATE, new String[]{ImageLogic.FORMATTER.format(expectedImage.getDate())});

        //idealy every test should be in its own method
        Image returnedImage = iLogic.createEntity(imageMap);
        int expectedImageId1 = Integer.parseInt(imageMap.get(ImageLogic.ID)[0]);
        int returnedImageID1 = returnedImage.getId();
        assertEquals(expectedImageId1,returnedImageID1 );
        assertEquals(imageMap.get(ImageLogic.TITLE)[0], returnedImage.getTitle());
        assertEquals(imageMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(imageMap.get(ImageLogic.LOCAL_PATH)[0], returnedImage.getLocalPath());

        imageMap = new HashMap<>();
        imageMap.put(ImageLogic.ID, new String[]{Integer.toString(1)});
        imageMap.put(ImageLogic.TITLE, new String[]{generateString.apply(1000)});
        imageMap.put(ImageLogic.URL, new String[]{generateString.apply(255)});
        imageMap.put(ImageLogic.LOCAL_PATH, new String[]{generateString.apply(255)});
        imageMap.put(ImageLogic.BOARD_ID, new String[]{id});
        imageMap.put(ImageLogic.DATE, new String[]{ImageLogic.FORMATTER.format(expectedImage.getDate())});
        //idealy every test should be in its own method
        returnedImage = iLogic.createEntity(imageMap);
         int expectedImageId2 = Integer.parseInt(imageMap.get(ImageLogic.ID)[0]);
        int returnedImageID2 = returnedImage.getId();
        assertEquals(expectedImageId2, returnedImageID2);
        assertEquals(imageMap.get(ImageLogic.TITLE)[0], returnedImage.getTitle());
        assertEquals(imageMap.get(ImageLogic.URL)[0], returnedImage.getUrl());
        assertEquals(imageMap.get(ImageLogic.LOCAL_PATH)[0], returnedImage.getLocalPath());
         }
    
    /**
     * Test get column names.
     */
    @Test
    final void testGetColumnNames(){
        List<String> list = iLogic.getColumnNames();
        assertEquals(Arrays.asList("ID", "BoardID", "Title", "URL", "LocalPath", "Date"),list);}
    
    /**
     * Test get column codes.
     */
    @Test
    final void testGetColumnCodes(){
        List<String> list = iLogic.getColumnCodes();
      assertEquals(Arrays.asList(ImageLogic.ID, ImageLogic.BOARD_ID, ImageLogic.TITLE, ImageLogic.URL, ImageLogic.LOCAL_PATH, ImageLogic.DATE),list);}
    
    /**
     * Test extract data as list.
     */
    @Test
    final void testExtractDataAsList(){
        List<?> list = iLogic.extractDataAsList(expectedImage);

        assertEquals(expectedImage.getId(), list.get(0));
        assertEquals(expectedImage.getBoard().getId(), list.get(1));
        assertEquals(expectedImage.getTitle(), list.get(2));
        assertEquals(expectedImage.getUrl(), list.get(3));
        assertEquals(expectedImage.getLocalPath(), list.get(4));
        assertEquals(expectedImage.getDate(), list.get(5)  );
        
    }
    
    }

