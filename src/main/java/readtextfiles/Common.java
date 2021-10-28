package readtextfiles;

/**
 * class contains common methods
 *
 * @author Pooja Singla
 */
public class Common
{
    static StringBuilder sb = new StringBuilder();

    public static String getPathToTargetFile(String[] args)
    {
        if (args.length >= 1)
        {
            return args[0];
        }
        return "src/test/resources/text.txt";
    }
}