package com.coolspy3.hypixelapi;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.coolspy3.csmodloader.mod.Entrypoint;
import com.coolspy3.csmodloader.mod.Mod;
import com.coolspy3.csmodloader.network.PacketHandler;
import com.coolspy3.csmodloader.network.SubscribeToPacketStream;
import com.coolspy3.cspackets.datatypes.MCColor;
import com.coolspy3.util.ClientChatReceiveEvent;
import com.coolspy3.util.ModUtil;

@Mod(id = "cshypixelapi", name = "CSHypixelAPI",
        description = "Provides an interface to Hypixel's public API", version = "2.1.1",
        dependencies = {"csmodloader:[1.3.1,2)", "cspackets:[1.2.1,2)", "csutils:[1.1.1,2)"})
public class HypixelAPI implements Entrypoint
{

    public static final String uuidRegex =
            "([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})";
    public static final String keyRegex = "Your new API key is " + uuidRegex;
    public static final Pattern keyPattern = Pattern.compile(keyRegex);
    public boolean awaitAPIKey = false;

    @Override
    public Entrypoint create()
    {
        return new HypixelAPI();
    }

    @Override
    public void init(PacketHandler handler)
    {
        handler.register(this);
        handler.register(new Command(this));
    }

    @SubscribeToPacketStream
    public void onChatMessageReceived(ClientChatReceiveEvent event)
    {
        if (awaitAPIKey)
        {
            String msg = event.msg;
            Matcher matcher = keyPattern.matcher(msg);
            if (matcher.matches())
            {
                try
                {
                    String apiKey = matcher.group(1);
                    APIConfig.getInstance().apiKey = UUID.fromString(apiKey);
                    ModUtil.sendMessage(MCColor.AQUA + "API key set to: \"" + apiKey + "\"");
                    APIConfig.save();
                }
                catch (IllegalArgumentException e)
                {
                    ModUtil.sendMessage(MCColor.RED + e.getMessage());
                }
                catch (IOException e)
                {
                    e.printStackTrace(System.err);
                }
                awaitAPIKey = false;
            }
        }
    }

}
