package lazyTrees;

import java.io.File;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * An object of type FoothillTunesStore creates an object of type MillionSongDataSubset, which in turn parses a JSON data set. The parsed data set is stored in an array of SongEntry objects.
 * Creates library of user requested songs.
 * Implements BST with lazy deletion to keep track of total tunes ("deleted" + non deleted)
 * and current tunes (non deleted only).
 *
 * 
 */
public class FoothillTunesStore {


    public static final boolean SHOW_DETAILS = true;

    // TODO: Define the functor class PrintObject to traverse and print out data from LazySearchTree
    PrintObject<SongEntry> printObject = new PrintObject<SongEntry>();

    // The data structure, which we use to add and remove SongEntrys.
    private LazySearchTree<SongEntry> tunes;

    // Instantiates tunes to be a LazySearchTree of SongEntry objects.
    public FoothillTunesStore()
    {
        tunes = new LazySearchTree<SongEntry>();
    }

    /**
     * Add a new SongEntry with the name as in parameter into tunes. If there is already same name product,
     * increase amount by one, if not create a new object.
     * @param SongEntry		The SongEntry to be added to the tunes tree.
     */
    public void addTotunes(String title, int duration, String artist_name, String genre)
    {
        // Create a temporary object to hold the SongEntry.
        SongEntry tmp = new SongEntry(title, duration,artist_name,genre);


        // Check if the SongEntry is in the tunes tree.
        boolean isFound = tunes.contains(tmp);

        if (!isFound)
        {
            // TODO: Modify insert method to work with lazy deletion such that it updates
            // both hard and soft sizes.
            tunes.insert(tmp);

            // NOTE: Need to check if the SongEntry was lazily deleted, then we need to increment the count
            SongEntry found = tunes.find(tmp);
            if (found.getCount() == 0)
            {
                found.incrementCount();
            }

            // avoid double incrementing a newly inserted SongEntry
            return;
        }

        // If the SongEntry is found, increase the number of SongEntrys in that SongEntry category.
        SongEntry found = tunes.find(tmp);

        // SongEntry was previously in tree, so increment the count
        found.incrementCount();
    }

    /**
     * If the SongEntry is in the tunes, decrease the count by one.
     * If only one SongEntry is left, remove it from the tunes.
     * @param SongEntry		The SongEntry to be removed to the tunes tree.
     */
    public void removeFromtunes(String title, int duration, String artist_name, String genre)
    {
        SongEntry tmp = new SongEntry(title, duration, artist_name, genre );
        boolean isFound = tunes.contains(tmp);

        // check if the SongEntry exists in the tunes disregarding lazy deletion
        if (!isFound)
        {
            throw new NoSuchElementException();
        }

        SongEntry found = tunes.find(tmp);

        // if the SongEntrys has zero left in stock,
        // then treat it as if it does not exist in the tree.
        if (found.getCount() == 0)
        {
            throw new NoSuchElementException();
        }
        // if the SongEntry has one left in stock,
        // then decrement its count and lazy delete it in the tree.
        else if (found.getCount() == 1)
        {
            found.decrementCount();

            // TODO: Modify to be a lazy deletion remove method, which marks
            //       found nodes as "deleted".
            tunes.remove(tmp);
        }
        // otherwise, simply decrement its count.
        else
        {
            found.decrementCount();
        }
    }


    /**
     * Display the first SongEntry and last time of the soft tree in lexical order.
     */
    public void showFirstAndLastSongEntry(String message)
    {
        System.out.println("\n" + message);

        // TODO: Modify the protected methods findMin() and findMax() to implement lazy deletion.
        //       Searches from the root of the tree and return sthe minimum and maximum node that
        //       has NOT been "deleted".
        try
        {
            SongEntry min = tunes.findMin();
            System.out.println ( "First item: " + min.toString());
        }
        catch (Exception NoSuchElementException)
        {
            System.out.println("Warning: minimum element not found!");
        }

        try
        {
            SongEntry max = tunes.findMax();
            System.out.println ( "Last item: " + max.toString());
        }
        catch (Exception NoSuchElementException)
        {
            System.out.println("Warning: minimum element not found!");
        }

    }

    /**
     * Shows the state of the tree by displaying:
     * - the *hard* tunes, which includes deleted nodes.
     * - the *soft* tunes, which excludes deleted nodes.
     * @param message	Additional details about the state.
     * @param showTree	Set to true if we want to display the contents of the tree
     */
    protected void displaytunesState(String message, boolean showTree)
    {
        System.out.println("\n" + message);
        System.out.println("\"hard\" number of unique SongEntrys (i.e. mSizeHard) = " + tunes.sizeHard());
        System.out.println("\"soft\" number of unique SongEntrys (i.e. mSize) = " + tunes.size());

        if (!showTree)
            return;

        System.out.println( "\nTesting traversing \"hard\" tunes:");

        // TODO: First, rename the public/private pair traverse() method of FHsearch_tree to traverseHard() method.
        //       Then, reuse this public/private pair of methods to traverses the tree
        //       and displays all the nodes.
        // NOTE: Here, we call the public version.
        tunes.traverseHard(printObject);


        System.out.println( "\n\nTesting traversing \"soft\" tunes:");

        // TODO: Define a public/private pair of methods that traverses the tree
        //       and displays only nodes that have not been lazily deleted.
        // NOTE: Here, we call the public version.
        tunes.traverseSoft(printObject);
        System.out.println("\n");
    }

    public static void main(String[] args)
    {
        final String DATAFILE = "resources/music_genre_subset.json";

//		final String TESTFILE = "resources/tunes.txt";

        // NOTE: An example of testing the boundary condition where we remove from empty playlist
//		final String TESTFILE = "resources/tunes_truncated.txt";

        // NOTE: An example of testing the boundary condition when all playlists are empty
        final String TESTFILE = "resources/tunes_empty.txt";

        // parses the JSON input file
        MillionSongDataSubset msd = new MillionSongDataSubset(DATAFILE);

        // retrieves the parsed objects
        SongEntry [] allSongs = msd.getArrayOfSongs();

        ArrayList<SongEntry> songList = new ArrayList<SongEntry>(Arrays.asList(allSongs));

        // displays the number of songs read from the input file
        System.out.printf("Welcome! We have over %d in FoothillTunes store! \n", songList.size());

        /// calls the class Jukebox to read in the test file, which includes user's request
        //  of which SongEntry objects to add to which playlist queue.


        System.out.printf("Test file: %s \n", TESTFILE);

        FoothillTunesStore myTunes = new FoothillTunesStore();

        File infile = new File(TESTFILE);


        try
        {
            Scanner input = new Scanner(infile);

            String line = "";
            int lineNum = 0;

            while (input.hasNextLine())
            {
                lineNum++;
                line = input.nextLine();
                String [] tokens = line.split(",");

                String selection = tokens[0];
                String SongEntryName = tokens[1];

                String message = "at line #" + lineNum + ": " + line;

                // When an SongEntry is added:
                // If the SongEntry is not in our tunes,
                // create a new entry in our tunes.
                // Otherwise, increment the count of the SongEntry.
                if (selection.equals("add"))
                {
                    for(int i = 0; i < songList.size(); i++)
                    {
                        if(songList.get(i).getTitle().equals(tokens[1]))
                        {

                            myTunes.addTotunes(songList.get(i).getTitle(), songList.get(i).getDuration(),
                                    songList.get(i).getArtistName(), songList.get(i).getGenre());
                            break;
                        }
                    }

                    // NOTE: Currently displaying the contents is disabled to reduce cluttering the output.
                    // Suggestion: To start, enable displaying the contents of the tree to help you debug.
                    if (SHOW_DETAILS)
                        myTunes.displaytunesState("\nUpdate " + message, true);
                }

                // When an SongEntry is bought:
                // Decrement the count of the SongEntry.
                // If the SongEntry is out of stock,
                // remove the SongEntry from tunes.
                //
                // Note: buying an out of stock SongEntry, is invalid. Handle it appropriately.
                else if (selection.equals("buy"))
                {
                    try
                    {
                        for(int j = 0; j < songList.size(); j++)
                        {
                            if(songList.get(j).getTitle().equals(tokens[1]))
                            {

                                myTunes.removeFromtunes(songList.get(j).getTitle(), songList.get(j).getDuration(),
                                        songList.get(j).getArtistName(), songList.get(j).getGenre());
                            }
                        }

                        // NOTE: Currently displaying the contents is disabled to reduce cluttering the output.
                        // Suggestion: To start, enable displaying the contents of the tree to help you debug.
                        if (SHOW_DETAILS)
                            myTunes.displaytunesState("\nUpdate " + message, true);
                    }
                    catch (java.util.NoSuchElementException ex)
                    {
                        // Note: Ideally we'd print to the error stream,
                        // but to allow correct interleaving of the output
                        // we'll use the regular output stream.
                        System.out.printf("\nWarning: Unable to fulfill request: %s \n", message);
                        System.out.printf("Warning: SongEntry %s is out of stock.\n", SongEntryName);
                    }
                }
                else
                {
                    System.out.println("Warning: tunes selection not recognized!");
                }

                // Display the first SongEntry and the last SongEntry before checking
                // if it's time to clean up our tunes.
                if (SHOW_DETAILS)
                    myTunes.showFirstAndLastSongEntry(message);
            }
            input.close();

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        // Display the tunes
        System.out.println("\n");
        myTunes.displaytunesState("\nFinal state of tunes:", true);

        // flush the error stream
        System.err.flush();

        System.out.println("\nDone with FoothillTunesStore.");
    }
}
