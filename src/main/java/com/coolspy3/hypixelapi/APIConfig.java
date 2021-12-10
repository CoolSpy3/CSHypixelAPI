package com.coolspy3.hypixelapi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Function;

import com.coolspy3.csmodloader.GameArgs;
import com.coolspy3.cspackets.datatypes.MCColor;
import com.coolspy3.util.ModUtil;

import com.google.gson.Gson;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.apache.ApacheHttpClient;
import net.hypixel.api.http.HypixelHttpClient;

public class APIConfig
{

    public static final Function<UUID, HypixelHttpClient> DEFAULT_CLIENT_CONSTRUCTOR =
            ApacheHttpClient::new;

    public UUID apiKey = null;

    // Base Config Code

    private static final File cfgFile =
            GameArgs.get().gameDir.toPath().resolve("hypixelapi.cfg.json").toFile();
    private static APIConfig INSTANCE = new APIConfig();

    public static APIConfig getInstance()
    {
        return INSTANCE;
    }

    public UUID getAPIKey() throws IOException
    {
        if (apiKey == null)
        {
            load();
        }
        return INSTANCE.apiKey;
    }

    public static HypixelAPI requireAPI() throws IOException
    {
        return requireAPI(DEFAULT_CLIENT_CONSTRUCTOR);
    }

    public static HypixelAPI requireAPI(Function<UUID, HypixelHttpClient> client) throws IOException
    {
        UUID apiKey = getInstance().getAPIKey();
        if (apiKey == null)
        {
            ModUtil.sendMessage(MCColor.RED + "Hypixel API is not linked!");
            ModUtil.sendMessage(MCColor.RED
                    + "Ensure that the Hypixel API mod is installed and run \"/linkhypixelapi\"");

            return null;
        }
        return new HypixelAPI(client.apply(apiKey));
    }

    public static void load() throws IOException
    {
        if (!cfgFile.exists())
        {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(cfgFile)))
        {
            String data = "", line;
            while ((line = reader.readLine()) != null)
            {
                data += line;
                data += "\n";
            }
            data = data.substring(0, data.length() - 1);
            Gson gson = new Gson();
            INSTANCE = gson.fromJson(data, APIConfig.class);
        }
    }

    public static void save() throws IOException
    {
        cfgFile.createNewFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cfgFile)))
        {
            Gson gson = new Gson();
            writer.write(gson.toJson(getInstance()));
        }
    }

}
