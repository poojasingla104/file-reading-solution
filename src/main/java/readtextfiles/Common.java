package readtextfiles;

/**
 * class contains common methods
 *
 * @author Pooja Singla
 */
class Common
{
    static StringBuilder sb = new StringBuilder();

    static String getPathToTargetFile(String[] args)
    {
        if (args.length >= 1)
        {
            return args[0];
        }
        return "/Users/poojasingla/text.txt";
    }
}