package fr327.theshark34.openlauncherlib.configuration.api.json;

import fr327.flowarg.openlauncherlib.ModifiedByFlow;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by NeutronStars on 14/07/2017
 */
@ModifiedByFlow
public final class JSONReader
{
    private final Logger logger;
    private final String json;

    public JSONReader(Logger logger, String path) throws IOException
    {
        this(logger, Paths.get(path));
    }

    public JSONReader(Logger logger, Path file) throws IOException
    {
        this(logger, Files.newBufferedReader(file, StandardCharsets.UTF_8));
    }

    public JSONReader(Logger logger, BufferedReader reader) throws IOException
    {
        this.logger = logger;
        this.json = this.load(reader);
    }

    private String load(BufferedReader reader) throws IOException
    {
        StringBuilder builder = new StringBuilder();

        while (reader.ready()) builder.append(reader.readLine());

        reader.close();

        return builder.length() == 0 ? "[]" : builder.toString();
    }

    public static <E> List<E> toList(Logger logger, String path)
    {
        return toList(logger, Paths.get(path));
    }

    public static <E> List<E> toList(Logger logger, Path file)
    {
        if (Files.notExists(file)) return new ArrayList<>();
        try
        {
            return toList(logger, Files.newBufferedReader(file));
        } catch (IOException e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
	public static <E> List<E> toList(Logger logger, BufferedReader bufferedReader)
    {
        List<E> list = new ArrayList<>();

        try
        {
            JSONReader reader = new JSONReader(logger, bufferedReader);
            JSONArray array = reader.toJSONArray();
            for (int i = 0; i < array.length(); i++)
            {
                try
                {
                    list.add((E)array.get(i));
                } catch (ClassCastException ignored) {}
            }
        } catch (IOException e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return list;
    }

    public static <V> Map<String, V> toMap(Logger logger, String path)
    {
        return toMap(logger, Paths.get(path));
    }

    public static <V> Map<String, V> toMap(Logger logger, Path file)
    {
        if (Files.notExists(file)) return new HashMap<>();
        try
        {
            return toMap(logger, Files.newBufferedReader(file));
        } catch (IOException e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return new HashMap<>();
    }

    @SuppressWarnings("unchecked")
	public static <V> Map<String, V> toMap(Logger logger, BufferedReader bufferedReader)
    {
        Map<String, V> map = new HashMap<>();

        try
        {
            JSONReader reader = new JSONReader(logger, bufferedReader);
            JSONObject object = reader.toJSONObject();
            for (String key : object.keySet())
            {
                try
                {
                    map.put(key, (V)object.get(key));
                } catch (ClassCastException ignored) {}
            }
        } catch (IOException e)
        {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return map;
    }

    public JSONArray toJSONArray()
    {
        return new JSONArray(json);
    }

    public JSONObject toJSONObject()
    {
        return new JSONObject(json);
    }

    public Logger getLogger()
    {
        return logger;
    }
}
