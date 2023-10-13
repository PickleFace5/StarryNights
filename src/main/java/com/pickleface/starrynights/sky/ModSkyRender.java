package com.pickleface.starrynights.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.pickleface.starrynights.config.ClientConfig;
import com.pickleface.starrynights.StarryNights;
import com.pickleface.starrynights.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = StarryNights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSkyRender {
    private static boolean createdStars = false;
    private static VertexBuffer starBuffer;
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void renderSky(RenderLevelStageEvent event) {
        if (!CommonConfig.modEnabled) return;

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        if (level.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL) return;

        LevelRenderer renderer = event.getLevelRenderer();
        if (!createdStars) {
            createStars(renderer);
            createdStars = true;
            return;
        }

        float f10 = level.getStarBrightness(event.getPartialTick()) * (1.0F - level.getRainLevel(event.getPartialTick()));
        if (f10 > 0.0F) {
            if (GameRenderer.getPositionColorShader() == null) return;
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            FogRenderer.setupNoFog();

            starBuffer.bind();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            PoseStack poseStack = event.getPoseStack();

            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
            poseStack.mulPose(Axis.ZP.rotationDegrees(level.getTimeOfDay(event.getPartialTick()) * 360 + 180));

            starBuffer.drawWithShader(event.getPoseStack().last().pose(), event.getProjectionMatrix(), GameRenderer.getPositionColorShader());
            VertexBuffer.unbind();

            poseStack.popPose();

            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
        }
    }

    private static void createStars(LevelRenderer levelRenderer) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

        starBuffer.bind();
        starBuffer.upload(drawStars(bufferBuilder));
        VertexBuffer.unbind();
        LOGGER.debug("Created new star buffer");

        if (levelRenderer.starBuffer != null) {
            levelRenderer.starBuffer.bind();
            levelRenderer.starBuffer.upload(createEmptyBuffer(bufferBuilder));
            VertexBuffer.unbind();
        }
        LOGGER.debug("Replaced vanilla buffer with empty one (this is to avoid openGL from spamming the logs with warnings)");
    }

    private static BufferBuilder.RenderedBuffer drawStars(BufferBuilder bufferBuilder) {
        RandomSource randomSource = RandomSource.create(ClientConfig.starSeed);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ModSkyRender.class.getResourceAsStream("/assets/star/stars.txt"))));
            int successes = 0, fails = 0;
            for (String line; (line = reader.readLine()) != null;) {
                int id = Integer.parseInt(line.substring(0, StringUtils.ordinalIndexOf(line, ",", 1)));
                float mag, ci;
                double x, y, z;
                try {
                    mag = Float.parseFloat(line.substring(StringUtils.ordinalIndexOf(line, ",", 3) + 1, StringUtils.ordinalIndexOf(line, ",", 4)));
                    ci = Float.parseFloat(line.substring(StringUtils.ordinalIndexOf(line, ",", 4) + 1, StringUtils.ordinalIndexOf(line, ",", 5)));
                    x = Double.parseDouble(line.substring(StringUtils.ordinalIndexOf(line, ",", 5) + 1, StringUtils.ordinalIndexOf(line, ",", 6)));
                    z = Double.parseDouble(line.substring(StringUtils.ordinalIndexOf(line, ",", 6) + 1, StringUtils.ordinalIndexOf(line, ",", 7)));
                    y = Double.parseDouble(line.substring(StringUtils.ordinalIndexOf(line, ",", 7) + 1, StringUtils.ordinalIndexOf(line, ",", 8))) * -1;
                } catch (NumberFormatException exception) {
                    LOGGER.trace("Star id {} threw NumberFormatException!", id);
                    fails++;
                    continue;
                }
                double d4 = 1.0D / Math.sqrt(z * z + y * y + x * x);
                x *= d4;
                y *= d4;
                z *= d4;
                double d5 = z * 300.0D;
                double d6 = y * 300.0D; // Due to minecraft weirdness, x and z are swapped. Not the biggest change in the world, but makes that code kinda messy :(
                double d7 = x * 300.0D;
                double d8 = Math.atan2(z, x);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);
                double d11 = Math.atan2(Math.sqrt(z * z + x * x), y);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);
                double d14 = randomSource.nextDouble() * Math.PI * 2.0D;
                double d15 = Math.sin(d14);
                double d16 = Math.cos(d14);

                float[] color = ColorIndexToRGB.convertToRGB_BV(ci);

                for (int j = 0; j < 4; ++j) {
                    double d18 = (double) ((j & 2) - 1) * 0.35;
                    double d19 = (double) ((j + 1 & 2) - 1) * 0.35;
                    double d21 = d18 * d16 - d19 * d15;
                    double d22 = d19 * d16 + d18 * d15;
                    double d23 = d21 * d12 + 0.0D * d13;
                    double d24 = 0.0D * d12 - d21 * d13;
                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;
                    bufferBuilder.vertex(d5 + d25, d6 + d23, d7 + d26).color(color[0], color[1], color[2], convertMagToAlpha(mag)/255).endVertex();
                }
                successes++;
            }
            LOGGER.info("Successfully created {} stars (skipped an additional {} stars. These are usually due to missing data values)", successes, fails);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while creating starBuffer, ending...");
        }
        return bufferBuilder.end();
    }

    private static BufferBuilder.RenderedBuffer createEmptyBuffer(BufferBuilder bufferBuilder) {
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        return bufferBuilder.end();
    }

    private static float convertMagToAlpha(float mag) {
        return (float) (255 * Math.pow(Math.exp(1), 0.9 * mag));
    }
}
