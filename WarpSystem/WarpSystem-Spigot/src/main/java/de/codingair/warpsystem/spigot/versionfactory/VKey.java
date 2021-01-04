package de.codingair.warpsystem.spigot.versionfactory;

public enum VKey {
    CooldownButton(VFac.OBJECTS),
    RotationItemComponent(VFac.OBJECTS),
    AnimationPartColor(VFac.OBJECTS),
    AnimationPartSpeed(VFac.OBJECTS),
    DestinationPageHandler(VFac.OBJECTS),
    ParticlesHandler(VFac.OBJECTS),
    PermissionButton(VFac.OBJECTS),
    CostsButton(VFac.OBJECTS),
    ParticleOptionsSpeed(VFac.OBJECTS),
    ParticleOptionsColor(VFac.OBJECTS),
    PortalBlockEditorHandler(VFac.OBJECTS),
    RTP_Go_Command_Handler(VFac.OBJECTS),
    WarpGUIChoosePage(VFac.OBJECTS),
    GWarpsItemUpdateHandler(VFac.OBJECTS),
    MessageButton(VFac.OBJECTS),
    CWarpSystem(VFac.OBJECTS),
    ;

    private final String path;

    VKey(String path) {
        this.path = path;
    }

    VKey() {
        this(null);
    }

    public String getPath() {
        return path + name();
    }
}
