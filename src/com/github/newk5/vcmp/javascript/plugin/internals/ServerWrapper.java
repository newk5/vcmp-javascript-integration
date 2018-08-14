package com.github.newk5.vcmp.javascript.plugin.internals;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Value;
import com.github.newk5.vcmp.javascript.ServerEventHandler;
import static com.github.newk5.vcmp.javascript.ServerEventHandler.timerRegistry;
import com.github.newk5.vcmp.javascript.plugin.core.Context;
import static com.github.newk5.vcmp.javascript.plugin.core.Context.console;
import static com.github.newk5.vcmp.javascript.plugin.core.Context.server;
import com.maxorator.vcmp.java.plugin.integration.generic.Colour;
import com.maxorator.vcmp.java.plugin.integration.generic.Vector;
import com.maxorator.vcmp.java.plugin.integration.placeable.Pickup;
import com.maxorator.vcmp.java.plugin.integration.player.Player;
import com.maxorator.vcmp.java.plugin.integration.server.WeaponAndAmmo;
import com.maxorator.vcmp.java.plugin.integration.vehicle.Vehicle;
import com.maxorator.vcmp.java.plugin.integration.vehicle.VehicleColours;
import com.maxorator.vcmp.java.tools.timers.TimerHandle;
import io.alicorn.v8.annotations.JSIgnore;

public class ServerWrapper {

    public ServerWrapper() {
    }

    //this will override some server functions so that they're more "javascript friendly"
    @JSIgnore
    public void overrideFunctions(V8 v8) {
        v8.executeVoidScript("server.kick = function(player) { return _ServerOverride_.kickPlayer(player); }");
        v8.executeVoidScript("server.getAllPlayers = function() { return _ServerOverride_.getAllPlayers(); }");
        v8.executeVoidScript("server.getAllVehicles = function() { return _ServerOverride_.getAllVehicles(); }");
        v8.executeVoidScript("server.setAltitudeLimit = function(value) { return _ServerOverride_.setAltitudeLimit(value); }");
        v8.executeVoidScript("server.setWaterLevel = function(value) { return _ServerOverride_.setWaterLevel(value); }");
        v8.executeVoidScript("server.setGameSpeed = function(value) { return _ServerOverride_.setGameSpeed(value); }");
        v8.executeVoidScript("server.setGravity = function(value) { return _ServerOverride_.setGravity(value); }");
        v8.executeVoidScript("server.createVehicle = function( modelId,  worldId,  position,  angle,  colours) { return _ServerOverride_.createVehicle(modelId,  worldId,  position,  angle,  colours); }");
        v8.executeVoidScript("server.setHandlingRule = function( modelId,  ruleIndex,  value) { return _ServerOverride_.setHandlingRule(modelId,  ruleIndex,  value); }");
        v8.executeVoidScript("server.addClass = function( teamId,  colour,  modelId,  x,  y,  z,  angle,  weapon1, ammo1 ,weapon2, ammo2, weapon3, ammo3 ) { return _ServerOverride_.addClass(teamId,  colour,  modelId,  x,  y,  z,  angle,  weapon1, ammo1 ,weapon2, ammo2, weapon3, ammo3 ); }");

    }

    public Vehicle createVehicle(int modelId, int worldId, Vector position, Object angle, VehicleColours colours) {
        Vehicle v = server.createVehicle(modelId, worldId, position.getX(), position.getY(), position.getZ(), new Float(angle + ""), colours.getPrimary(), colours.getSecondary());
        v.clearData();
        return v;
    }

    public void kickPlayer(Player p) {
        timerRegistry.register(false, 50, () -> {
            p.kick();
        });
    }

    public void setGravity(Object value) {
        server.setGravity(new Float(value + ""));
    }

    public void setGameSpeed(Object value) {
        server.setGameSpeed(new Float(value + ""));
    }

    public void setWaterLevel(Object value) {
        server.setWaterLevel(new Float(value + ""));
    }

    public void setAltitudeLimit(Object value) {
        server.setAltitudeLimit(new Float(value + ""));
    }

    public void setHandlingRule(int modelId, int rule, Object value) {
        server.setHandlingRule(modelId, rule, new Double(value + ""));
    }

    public Integer addClass(int teamId, Colour colour, int modelId, Object x, Object y, Object z, Object angle, int weaponOne, int weaponOneAmmo, int weaponTwo, int weaponTwoAmmo, int weaponThree, int weaponThreeAmmo) {
        Vector v = null;
        try {
            v = new Vector(new Float(x + ""), new Float(y + ""), new Float(z + ""));
        } catch (Exception e) {
            console.error("Error - server.addClass, xyz coordinates must be floats!");
            return null;
        }
        return server.addPlayerClass(teamId, colour.getHex(), modelId, v.getX(), v.getY(), v.getZ(), new Float(angle + ""), weaponOne, weaponOneAmmo, weaponTwo, weaponTwoAmmo, weaponThree, weaponThreeAmmo);
    }

    public V8Array getAllPlayers() {
        V8Array v8Arr = new V8Array(Context.v8);

        for (Player p : server.getAllPlayers()) {
            V8Value player = (V8Value) Context.toJavascript(p);
            v8Arr.push(player);
        }
        return v8Arr;
    }

    public V8Array getAllVehicles() {
        V8Array v8Arr = new V8Array(Context.v8);

        for (Vehicle v : server.getAllVehicles()) {
            V8Value veh = (V8Value) Context.toJavascript(v);
            v8Arr.push(veh);
        }
        return v8Arr;
    }
}
