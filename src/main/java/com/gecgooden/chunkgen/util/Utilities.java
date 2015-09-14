package com.gecgooden.chunkgen.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.gecgooden.chunkgen.reference.Reference;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraftforge.common.DimensionManager;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;




public class Utilities {

	public static void generateChunks(int x, int z, int width, int height, int dimensionID) {

		ChunkProviderServer cps = MinecraftServer.getServer().worldServerForDimension(dimensionID).theChunkProviderServer;

		List<Chunk> chunks = new ArrayList<Chunk>(width*height);
		for(int i = (x - width/2); i < (x + width/2); i++) {
			for(int j = (z - height/2); j < (z + height/2); j++) {
				generateChunk(i, j, dimensionID);
			}
		}
		for(Chunk c : chunks) {
			cps.unloadChunksIfNotNearSpawn(c.xPosition, c.zPosition);
		}
	}

	private static boolean chunksExist(int x, int z, int dimensionID) {
		WorldServer world = null;
	
		world = DimensionManager.getWorld(dimensionID);
		
		return RegionFileCache.createOrLoadRegionFile(world.getChunkSaveLocation(), x, z).chunkExists(x & 0x1F, z & 0x1F);

		
	}
	
	public static void generateChunk(int x, int z, int dimensionID) {
		ChunkProviderServer cps = MinecraftServer.getServer().worldServerForDimension(dimensionID).theChunkProviderServer;
		if(!chunksExist(x, z, dimensionID)) {
			cps.loadChunk(x, z);

			cps.loadChunk(x, z+1);
			cps.loadChunk(x+1, z);
			cps.loadChunk(x+1, z+1);
			
			Reference.logger.info("Loaded Chunk at " + x + " " + z + " " + dimensionID);
		}
	}

	public static void queueChunkGeneration(ICommandSender icommandsender, int x, int z, int height, int width, int dimensionID) {
		for(int i = (x - width/2); i < (x + width/2); i++) {
            for(int j = (z - height/2); j < (z + height/2); j++) {
                if(Reference.toGenerate == null) {
                    Reference.toGenerate = new LinkedList<ChunkPosition>();
                }
                Reference.toGenerate.add(new ChunkPosition(i, j, dimensionID, icommandsender));
            }
        }
	}
}
