package com.vhbob.airimines.mines;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;

public class SchematicMine extends Mine {

    private BlockArrayClipboard schematic;

    public SchematicMine(Region mineBlocks, BlockArrayClipboard schematic) {
        this.mineBlocks = mineBlocks;
        this.schematic = schematic;
    }

    @Override
    public void reset() {
        super.reset();
        // Load blocks
        try (EditSession session = WorldEdit.getInstance().newEditSession(schematic.getRegion().getWorld())) {
            Operation operation = new ClipboardHolder(schematic)
                    .createPaste(session)
                    .to(mineBlocks.getMinimumPoint())
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            e.printStackTrace();
        }
    }
}
