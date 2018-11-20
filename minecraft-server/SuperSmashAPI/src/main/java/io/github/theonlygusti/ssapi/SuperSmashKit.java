package io.github.theonlygusti.ssapi;

import io.github.theonlygusti.doublejump.DoubleJumper;
import io.github.theonlygusti.ssapi.item.ItemAbility;
import io.github.theonlygusti.ssapi.passive.Passive;
import io.github.theonlygusti.ssapi.SuperSmashController;

import io.github.theonlygusti.ssapi.util.EffectOverTime;
import io.github.theonlygusti.ssapi.util.IntegerBoolean;
import io.github.theonlygusti.ssapi.util.IntegerDouble;
import io.github.theonlygusti.ssapi.util.IntegerVector;

import java.util.List;
import java.util.ArrayList;
import static java.lang.Math.*;

import me.libraryaddict.disguise.disguisetypes.Disguise;

import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/* About the Damage API:
    1. Initializing and Stopping the automatic background system:
       -to initialize an instanciated kit:
        call protected void init(Double initMaxHealth, Double initHealthRegenerationPerTick, Double initAttackDamage, Double initKnockbackDealed, Double initKnockbackTaken, Double[] initResistances){...}
        with the input beeing the base stats of the kit, from there the protected function (see 2.) should be able to handle any damage/knockback inflictions as well as stat modifiers!
       -to stop the background system which is working based on a BukkitRunnable, be sure to call preDekit(); before dekitting since otherwise the Runnable will keep going every tick -
        this should already be coded in SuperSmashController.dekit() {... playerKits.get(player).preDekit(); ...} so its automatically happening on dekitting
        
    2. Handling Damage and Knockback as well as their modifiers:
       -2.1: Inflicting Damage/Knockback:
       -2.2: Adding/clearing Modifiers:
       -2.3: Overwritable Functions for the actual kits:
             1.method      : manipulateDamageTaking is called before calling reduceHealth by takeDamage and represent the damage that was already merged with the proper Resistance
               declaration : public Double manipulateDamageTaking(SuperSmashKit by, String DamageType, Double amount, String info) 
               example     : lightning shield can check if the damage type is "Melee", if the shield is up , inflictDamage(by,...); inflictKnockback(by,...); return 0;
               return      : expects the damage that still should be taken (by your kit)
  
             2.method      : manipulateDamageDealing is called before calling takeDamage by inflictDamage
               declaration : public Double manipulateDamageDealing(SuperSmashKit to, String DamageType, Double amount, String info)
               example     : +1 for each ravage stack if damage type is "Melee"
               return      : expects the damage that will be handed to the damage calculation (of target kit)
  
             3.method      : manipulateKnockbackTaking is called before inflicting knockback to the player
               declaration : public Vector manipulateKnockbackTaking(SuperSmashKit by, String DamageType, Vector amount, String info)
               example     : less projectile knockback during guardian is under half health
               return      : expects the Vector that should be applied (to your kit)
  
             4.method      : manipulateKnockbackDealing is called before calling takeKnockback by inflictKnockback
               declaration : public Vector manipulateDamageDealing(SuperSmashKit to, String DamageType, Double amount, String info)
               example     : +1 for each ravage stack if damage type is "Melee"
               return      : expects the Vector that should be handed to the knockback handling (of target kit)
  
             5.method      : afterLosingHealth is called by takeDamage after it called reduceHealth
               declaration : public void afterLosingHealth(SuperSmashKit by, String DamageType, Double lostHealth, String info)
               example     : new Passive: "Witches Curse" as witch passive, weakening consecutive melee hits by 0.5, up to 3 stacks per player, each decays after 3 seconds, applied to the player(generally less melee damage dealed to any mob for a short time)
               return      : void
  
             6.method      : preExecution is called by execution, to give the chance for a potential revive / afterlife passive
               declaration : public Double manipulateDamageDealing(SuperSmashKit to, String DamageType, Double amount, String info)
               example     : +1 for each ravage stack if damage type is "Melee"
               return      : expects 0.0 or less to be dead, otherwise the Health amount to stay alive with, maybe a reviving passive or aftermath passive will be an idea(of your kit)
  
    3. EffectsOverTime:
       -EffectsOverTime are currently not completely implemented but provide a lot of potential of self and target affecting, ask me if you want something added

*/
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class SuperSmashKit implements DoubleJumper {
  public abstract Disguise getDisguise();
  public abstract List<ItemAbility> getItemAbilities();
  public abstract Player getPlayer();
  public abstract void doPunch();
  public abstract void doRightClick();
  public abstract void changeHeldItem(int previousSlot, int newSlot);
  public abstract ItemAbility getHeldItemAbility();
  public abstract List<Passive> getPassives();
  public abstract void shootBow(Arrow arrow);
  public abstract int getArmorValue();
  
   
   //Constant will be created with the kit and should not be changed, thats what the modifiers(Increase,Reduction,Multiplier) are for
  //DamageTypes wont ever be changed by the program and only serve the purpose to add a new type easier(by simply adding it here)
  //and allowing the initialization of the variables to be automatic from there, as well as giving the DamageTypeCount and allowing
  //Strings to be used as DamageType in function calls instead of int, while the int = the index of the String in this array
  //Adding a DamageType here will allow you to immediatly apply damage of that type with an ability, but in order for Kits to have
  //resistance against it, you will need to add the new damage type resistance in the kit initialization that ends up calling SuperSmashKit.init(...Double[] Resistances)
  private int taskID;
  protected Boolean SHOW_DEBUG_MESSAGES = false;
  protected Boolean DETAILED_DEBUG_MESSAGES = true;  //only matters while SHOW_DEBUG_MESSAGES == true  
  
  //not all DamageTypes need resistances, but the x resistances provided will be applied to the first x damage types
  protected static final String[] DamageTypes = { "True", "Melee", "Ability", "Projectile", "Explosion", "Fire", "Poison", "Wither" };
  private int DamageTypeCount = getDamageTypeCount(); //is initialized automatically
  private Double MaxHealth = 20.0;
  private Double HealthRegenerationPerTick = 0.0;

  private Double AttackDamage = 1.0;
  private Double KnockbackDealed = 1.0; //with Attacks since Abilities have their own Kb values; 1 = 100% so default
  private Double KnockbackTaken = 1.0;

  private Double[] Resistances = {0.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};

//Variables for private and public acess
  private Double Health = 20.0;
  private Double Hunger = 20.0;
  

  private IntegerBoolean AllowRegeneration = new IntegerBoolean(0, true); //Can technically be a custom Effect
  private IntegerBoolean AllowHealing = new IntegerBoolean(0, true);

  private List<EffectOverTime> Effects = new ArrayList(0); //EffectsOverTime have a bool "removeOnTouchingGround" a String ID and SuperSmashKit by for a class to check if it applied it

  private IntegerBoolean Invulnerable = new IntegerBoolean(0,false); //Will be caused to set true by applyGeneralInvulnerability(int Duration)
  private IntegerBoolean[] Invulnerabilities = new IntegerBoolean[getDamageTypeCount()]; //Custom Damage types, length DamageTypeCount(DTC)

  private List<IntegerDouble> MaxHealthIncreasesPreMult = new ArrayList(0);
  private List<IntegerDouble> MaxHealthReductionsPreMult = new ArrayList(0);
  private List<IntegerDouble> MaxHealthMultipliers = new ArrayList(0);
  private List<IntegerDouble> MaxHealthIncreasesPostMult = new ArrayList(0);
  private List<IntegerDouble> MaxHealthReductionsPostMult = new ArrayList(0);

  private List<IntegerDouble> HealthRegenerationIncreasesPreMult = new ArrayList(0);
  private List<IntegerDouble> HealthRegenerationReductionsPreMult = new ArrayList(0);
  private List<IntegerDouble> HealthRegenerationMultipliers = new ArrayList(0);
  private List<IntegerDouble> HealthRegenerationIncreasesPostMult = new ArrayList(0);
  private List<IntegerDouble> HealthRegenerationReductionsPostMult = new ArrayList(0);


  private List<IntegerDouble>[] ResistanceIncreasesPreMult   = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] ResistanceReductionsPreMult  = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] ResistanceMultipliers        = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] ResistanceIncreasesPostMult  = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] ResistanceReductionsPostMult = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]


  private List<IntegerDouble>[] TakenDamageIncreasesPreMult   = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] TakenDamageReductionsPreMult  = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] TakenDamageMultipliers        = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] TakenDamageIncreasesPostMult  = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] TakenDamageReductionsPostMult = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]

  private List<IntegerDouble>[] InflictedDamageIncreasesPreMult   = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] InflictedDamageReductionsPreMult  = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] InflictedDamageMultipliers        = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] InflictedDamageIncreasesPostMult  = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] InflictedDamageReductionsPostMult = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]

  private List<IntegerDouble>[] KnockbackTakenMultipliers            = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Since TRUE Damage wont deal knockback, DamageType 0/True will be for general modifiers
  private List<IntegerVector>[] KnockbackTakenDirectionalMultipliers = (ArrayList<IntegerVector>[])new ArrayList[DamageTypeCount]; 

  private List<IntegerDouble>[] KnockbackDealedMultipliers            = (ArrayList<IntegerDouble>[])new ArrayList[DamageTypeCount]; //Since TRUE Damage wont deal knockback, DamageType 0/True will be for general modifiers
  private List<IntegerVector>[] KnockbackDealedDirectionalMultipliers = (ArrayList<IntegerVector>[])new ArrayList[DamageTypeCount]; 

  
/////Variables that allow/deny functions that cause over time effects/modifiers; All init true
  
  protected IntegerBoolean Allow_applyMaxHealthReduction  = new IntegerBoolean(0, true);  //The Integer is the Timer until false becomes true again
  protected IntegerBoolean Allow_clearMaxHealthReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyMaxHealthIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearMaxHealthIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyMaxHealthMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearMaxHealthMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyHealthRegenerationReduction = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearHealthRegenerationReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyHealthRegenerationIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearHealthRegenerationIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyHealthRegenerationMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearHealthRegenerationMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyGeneralInvulnerability = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearGeneralInvulnerability = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyInvulnerability = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearInvulnerabilities = new IntegerBoolean(0, true); 

  
  protected IntegerBoolean Allow_applyResistanceReduction = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearResistanceReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyResistanceIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearResistanceIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyResistanceMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearResistanceMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyTakenDamageReduction = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearTakenDamageReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyTakenDamageIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearTakenDamageIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyTakenDamageMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearTakenDamageMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyDealedDamageReduction = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearDealedDamageReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyDealedDamageIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearDealedDamageIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyDealedDamageMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearDealedDamageMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyKnockbackTakenMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearKnockbackTakenMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyKnockbackTakenDirectionalMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearKnockbackTakenDirectionalMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyKnockbackDealedMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearKnockbackDealedMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean Allow_applyKnockbackDealedDirectionalMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_clearKnockbackDealedDirectionalMultipliers = new IntegerBoolean(0, true);

  
  protected IntegerBoolean Allow_EffectRemove = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_EffectExists = new IntegerBoolean(0, true);
  protected IntegerBoolean Allow_EffectApply = new IntegerBoolean(0, true);


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//VisualSupportWhileScrollingFast/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  //FUNCTIONS To Override if needed:
  //manipulateDamageTaking is called before calling reduceHealth
  //example: lightning shield can check if the damage type is "Melee", if yes, inflictDamage(by,...); inflictKnockback(by,...); return 0;
  public Double manipulateDamageTaking(SuperSmashKit by, String DamageType, Double amount, String info) {
    return amount;
  }
  //for example +1 for each ravage stack if damage type is "Melee"
  public Double manipulateDamageDealing(SuperSmashKit to, String DamageType, Double amount, String info) {
    return amount;
  }
  //for example less during guardian is under half health
  public Vector manipulateKnockbackTaking(SuperSmashKit by, String DamageType, Vector amount, String info) {
    return amount;
  }
  public Vector manipulateKnockbackDealing(SuperSmashKit to, String DamageType, Vector amount, String info) {
    return amount;
  }
  //maybe you want to get an effect based on the amount of damage you took, or curse an opponent
  // example new Passive: "Witches Curse" as witch passive, weakening consecutive melee hits by 0.5, up to 3 stacks per player, each decays after 3 seconds, applied to the player(generally less melee damage dealed to any mob for a short time)
  public void afterLosingHealth(SuperSmashKit by, String DamageType, Double lostHealth, String info) {
  }
  //return 0.0 or less to be dead, maybe a reviving passive or aftermath passive will be an idea
  public Double preExecution(SuperSmashKit by, String DamageType, Double fatalDamageAmount, String info) {
    return 0.0;
  }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//VisualSupportWhileScrollingFast/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  //FUNCTIONS
  
  protected int getDamageTypeCount(){
    return DamageTypes.length;  
    }

  //Convert a String DamageType into the connected int; returns -1 if invalid
  protected int stringDamageTypeToInt(String DamageType) {
    for ( int i = 0; i < getDamageTypeCount() ; i++ ) {
      if (DamageTypes[i] == DamageType) {
        return i;  
      } 
    }
    return -1;
  }
  
  protected String intDamageTypeToString(int DamageType) {
    return DamageTypes[DamageType];
  }
  
  protected Double getHealth() {
    return Health;
  }
  
  protected Double getHealthPercentage() {
    return (100/MaxHealth*Health);
  }

  
  private List<IntegerDouble>[] initHelp(List<IntegerDouble>[] toChange) {
    for ( int i = 0 ; i < DamageTypeCount ; i++ ) {
      toChange[i] = new ArrayList(0);
    }
    return toChange;
  }
 
 private List<IntegerVector>[] initHelp2(List<IntegerVector>[] toChange) {
    for ( int i = 0 ; i < DamageTypeCount ; i++ ) {
      toChange[i] = new ArrayList(0);
    }
    return toChange;
  }
  
///////////////////  
  protected void init(Double initMaxHealth, Double initHealthRegenerationPerSecond, Double initAttackDamage, Double initKnockbackDealed, Double initKnockbackTaken, Double[] initResistances){
    //initialize the permanent values
	Double initHealthRegenerationPerTick = (initHealthRegenerationPerSecond / 20);
    if (initMaxHealth > 0) {
      MaxHealth = initMaxHealth;
    }
    HealthRegenerationPerTick = initHealthRegenerationPerTick;
    AttackDamage = initAttackDamage;
    KnockbackDealed = initKnockbackDealed;
    KnockbackTaken = initKnockbackTaken;
    
    Resistances = new Double[DamageTypeCount];
    
    for (int i = 0; i < initResistances.length; i++ ) {
      Resistances[i] = initResistances[i];
    }
      
    //initialize the rest of the kit  
    Health = MaxHealth;
  
    for ( int i = 0 ; i < DamageTypeCount ; i++ ) {
        Invulnerabilities[i] = new IntegerBoolean(0,false);
    }
  
    ResistanceIncreasesPreMult = initHelp(ResistanceIncreasesPreMult);
    ResistanceReductionsPreMult = initHelp(ResistanceReductionsPreMult);
    ResistanceMultipliers = initHelp(ResistanceMultipliers);
    ResistanceIncreasesPostMult = initHelp(ResistanceIncreasesPostMult);
    ResistanceReductionsPostMult = initHelp(ResistanceReductionsPostMult);

    TakenDamageIncreasesPreMult = initHelp(TakenDamageIncreasesPreMult);
    TakenDamageReductionsPreMult = initHelp(TakenDamageReductionsPreMult);
    TakenDamageMultipliers = initHelp(TakenDamageMultipliers);
    TakenDamageIncreasesPostMult = initHelp(TakenDamageIncreasesPostMult);
    TakenDamageReductionsPostMult = initHelp(TakenDamageReductionsPostMult);

    InflictedDamageIncreasesPreMult = initHelp(InflictedDamageIncreasesPreMult);
    InflictedDamageReductionsPreMult = initHelp(InflictedDamageReductionsPreMult);
    InflictedDamageMultipliers = initHelp(InflictedDamageMultipliers);
    InflictedDamageIncreasesPostMult = initHelp(InflictedDamageIncreasesPostMult);
    InflictedDamageReductionsPostMult = initHelp(InflictedDamageReductionsPostMult);

    KnockbackTakenMultipliers = initHelp(KnockbackTakenMultipliers);
    KnockbackTakenDirectionalMultipliers = initHelp2(KnockbackTakenDirectionalMultipliers);
    
    KnockbackDealedMultipliers = initHelp(KnockbackDealedMultipliers);
    KnockbackDealedDirectionalMultipliers = initHelp2(KnockbackDealedDirectionalMultipliers);

    
    getPlayer().sendMessage("init");
    taskID = this.getPlayer().getServer().getScheduler().scheduleSyncRepeatingTask( /*somehow get the plugin here*/SuperSmashController.getPlugin() , new  BukkitRunnable(){
     @Override
      public void run(){
        //DEBUG getPlayer().sendMessage("PreTick");  
        tick();
      }
    }, 1l, 1l);
   
  }
  
  protected void preDekit() {
    this.getPlayer().getServer().getScheduler().cancelTask(taskID);
  }
  
  protected Double calculateResistance(int DamageType) {
    Double ToReturn = Resistances[DamageType];
    //apply all modifiers
    
    for ( int i = 0 ; i < ResistanceIncreasesPreMult[DamageType].size() ; i++ ) {
      ToReturn = ToReturn + ResistanceIncreasesPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < ResistanceReductionsPreMult[DamageType].size() ; i++ ) {
      ToReturn = ToReturn - ResistanceReductionsPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < ResistanceMultipliers[DamageType].size() ; i++ ) {
      ToReturn = ToReturn * ResistanceMultipliers[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < ResistanceIncreasesPostMult[DamageType].size() ; i++ ) {
      ToReturn = ToReturn + ResistanceIncreasesPostMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < ResistanceReductionsPostMult[DamageType].size() ; i++ ) {
      ToReturn = ToReturn - ResistanceReductionsPostMult[DamageType].get(i).DoubleValue;
    }

    return ToReturn;
  }
  
  private Double calculateDamageWithResistances(int DamageType, Double amount) {
    Double resis = calculateResistance(DamageType);
    getPlayer().sendMessage("Resistance:" + Double.toString(resis));
    getPlayer().sendMessage("Damage    :" + Double.toString(amount));
    getPlayer().sendMessage("DamageType:" + Integer.toString(DamageType));
    getPlayer().sendMessage("calculated Damage :" + Double.toString((1.0-(resis/(resis+10.0)))*amount));
  //TODO: implement true damage calculation (not referring to the DamageType "TRUE" damage)
    return (1.0-(resis/(resis+10.0)))*amount;
  }
  
  private Double calculateDamageToBeTaken(int DamageType, Double amount) {     
    //apply all modifiers
    
    for ( int i = 0 ; i < TakenDamageIncreasesPreMult[DamageType].size() ; i++ ) {
      amount = amount + TakenDamageIncreasesPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < TakenDamageReductionsPreMult[DamageType].size() ; i++ ) {
      amount = amount - TakenDamageReductionsPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < TakenDamageMultipliers[DamageType].size() ; i++ ) {
      amount = amount * TakenDamageMultipliers[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < TakenDamageIncreasesPostMult[DamageType].size() ; i++ ) {
      amount = amount + TakenDamageIncreasesPostMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < TakenDamageReductionsPostMult[DamageType].size() ; i++ ) {
      amount = amount - TakenDamageReductionsPostMult[DamageType].get(i).DoubleValue;
    }

    return amount;
  }

  private Double calculateDamageToBeInflicted(int DamageType, Double amount) {
    //apply all modifiers
    
    for ( int i = 0 ; i < InflictedDamageIncreasesPreMult[DamageType].size() ; i++ ) {
      amount = amount + InflictedDamageIncreasesPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < InflictedDamageReductionsPreMult[DamageType].size() ; i++ ) {
      amount = amount - InflictedDamageReductionsPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < InflictedDamageMultipliers[DamageType].size() ; i++ ) {
      amount = amount * InflictedDamageMultipliers[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < InflictedDamageIncreasesPostMult[DamageType].size() ; i++ ) {
      amount = amount + InflictedDamageIncreasesPostMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < InflictedDamageReductionsPostMult[DamageType].size() ; i++ ) {
      amount = amount - InflictedDamageReductionsPostMult[DamageType].get(i).DoubleValue;
    }

    return amount;
  }
  
  //Is called by reduceHealth on fatal damage
  private void execution(SuperSmashKit by, int DamageType, Double amount, String info) {
    Double isStillAlive = preExecution(by, intDamageTypeToString(DamageType), amount, info);
    if (isStillAlive <= 0.0) {
      //Actual execution, stats, cause dekitting, . . .
      //TODO: look above
    }
    else {
      //Death was somehow prevented
      Health = isStillAlive;
    }
  }
  
  //Reduces Health and triggers the Execution function on death,
  //will return the Health that was left on death instead of the damage that was passed
  private Double reduceHealth(SuperSmashKit by, int DamageType, Double amount, String info) {
    Double ToReturn = 0.0; //Init the value that will be returned
    
    //if the damage is not lethal
    if (Health >= amount) {
      Health = Health - amount;  //reduce the health
      ToReturn = amount;         //return the amount of damage dealed
    }
    else {  //if the damage is lethal
      ToReturn = Health; //return the amount of health left as damage dealed
      Health = 0.0;      //reduce the health
      execution(by, DamageType, ToReturn, info);
    }
    
    refresh();  
    return ToReturn; 
  }
    
  private Double IncreaseHealth(Double amount) {
    Double ToReturn = 0.0; //Init the value that will be returned
    
    //if the healing will not overheal
    if (Health + amount <= MaxHealth) {
      Health = Health + amount;  //increase the health
      ToReturn = amount;         //return the amount of health healed
    }
    else {  //if the healing would overheal
      ToReturn = MaxHealth - Health; //return the amount of health healed
      Health = MaxHealth;      //increase the health
    }
     
    refresh();
    return ToReturn; 
  }
  
///////////////////////// 
  protected void inflictSelfDamage(Double amount) {
    reduceHealth(this, stringDamageTypeToInt("True"), amount, "");
  }

  private Double takeDamage(SuperSmashKit by, int DamageType, Double amount, String info) { //Info can be empty or can contain additions, for example an ability name
    Double dmgToBeTaken = 0.0;
    Double dmgTaken = 0.0;
    
    //Nothing happens if amount <= 0
    if (amount > 0.0) {
      //Check if invulnerable or invulnerable to that damage type
      if ((Invulnerable.BooleanValue)||(Invulnerabilities[DamageType].BooleanValue)) {
        //Nothing happens, ToReturn is already 0 
      }
      else {
        //calculate the damage, reduce the health and return the actually dealed damage
        dmgToBeTaken = calculateDamageWithResistances(DamageType, calculateDamageToBeTaken(DamageType, amount));
        dmgToBeTaken = manipulateDamageTaking(by, intDamageTypeToString(DamageType), dmgToBeTaken, info);
        dmgTaken = reduceHealth(by, DamageType, dmgToBeTaken, info);
        afterLosingHealth(by, intDamageTypeToString(DamageType), dmgTaken, info);
      }
    }
    
    
    return dmgTaken;
  }

//////////////////////////  
  //if the damage type is melee and the amount is -1 it will grab the provided melee damage
  protected Double inflictDamage(SuperSmashKit to, int DamageType, Double amount, String info) { //Info can be empty or can contain additions, for example an ability name
    Double dealedDamage = 0.0;
    Double damageToDeal = 0.0;
    
    //if the damage type is melee and the amount is -1 it will grab the provided melee damage
    if ((DamageType == 1)&&(amount == -1.0)) {
      amount = AttackDamage;
    }
    
    //Nothing happens if amount <= 0
    if (amount > 0.0) {
      damageToDeal = manipulateDamageDealing(to, intDamageTypeToString(DamageType), amount, info);
      dealedDamage = to.takeDamage(this, DamageType, calculateDamageToBeInflicted(DamageType, amount), info);
    }
    
    
    return dealedDamage;
  }
  protected Double inflictDamage(SuperSmashKit to, String DamageType, Double amount, String info) { //Info can be empty or can contain additions, for example an ability name
    return inflictDamage(to,stringDamageTypeToInt(DamageType),amount,info);
  }
  
  private void applyKnockback(Vector v) {
    getPlayer().setVelocity(v);
  }
  
  private Vector calculateKnockbackToBeTaken(int DamageType, Vector v) {
    Vector ToReturn = v.clone();
    for ( int i = 0 ; i<KnockbackTakenMultipliers[DamageType].size() ; i++) {
      ToReturn.multiply(KnockbackTakenMultipliers[DamageType].get(i).DoubleValue);
    }
    for ( int i = 0 ; i<KnockbackTakenDirectionalMultipliers[DamageType].size() ; i++) {
      ToReturn.multiply(KnockbackTakenDirectionalMultipliers[DamageType].get(i).VectorValue);
    }
    return ToReturn;
  }
  private Vector takeKnockback(SuperSmashKit by, int DamageType, Vector v, String info) { //Info can be empty or can contain additions, for example an ability name
    Vector toApply = calculateKnockbackToBeTaken(DamageType,v);
    toApply = manipulateKnockbackTaking(by, intDamageTypeToString(DamageType), toApply, info);
	applyKnockback(toApply);
    return toApply;
  }
  private Vector calculateKnockbackToBeInflicted(int DamageType, Vector v) {
    Vector ToReturn = v.clone();
    for ( int i = 0 ; i<KnockbackDealedMultipliers[DamageType].size() ; i++) {
      ToReturn.multiply(KnockbackDealedMultipliers[DamageType].get(i).DoubleValue);
    }
    for ( int i = 0 ; i<KnockbackDealedDirectionalMultipliers[DamageType].size() ; i++) {
      ToReturn.multiply(KnockbackDealedDirectionalMultipliers[DamageType].get(i).VectorValue);
    }
    return ToReturn;
  }
  protected Vector inflictKnockback(SuperSmashKit to, String DamageType, Vector v, String info) { //Info can be empty or can contain additions, for example an ability name
    return inflictKnockback(to,stringDamageTypeToInt(DamageType), v, info);
  }
  protected Vector inflictKnockback(SuperSmashKit to, int DamageType, Vector v, String info) { //Info can be empty or can contain additions, for example an ability name
    Vector toInflict = manipulateKnockbackDealing(to, intDamageTypeToString(DamageType), v.clone(), info);
	return to.takeKnockback(this, DamageType, calculateKnockbackToBeInflicted(DamageType,toInflict), info);
  }
  
/////////////////////////  
  protected Double heal(Double amount) {
    Double ToReturn = 0.0;
    //if healing is not disabled
    if (AllowHealing.BooleanValue) {
      ToReturn = IncreaseHealth(amount);
    }   
      
    return ToReturn;
  }

  private void onEffectOverTimeExpire() {   //called by processEffectsOverTime;  for example a custom damage over time effect that denys regeneration
    //No Effects that do something on Expiring yet
  }
  private void processEffectsOverTime() { //called by onTick

    for ( int i = 0 ; i < Effects.size() ; i++) {
      if (Effects.get(i).Timer > 0) {
        Effects.get(i).Timer = Effects.get(i).Timer - 1;
      }
    }
    
    //No EffectsOverTime declared yet
  }
  private List<IntegerDouble> processModifiersHelp(List<IntegerDouble> toChangeTicks) {
    List<IntegerDouble> toReturn = new ArrayList(0);
    toReturn.clear();
    
    for ( int i = 0 ; i < toChangeTicks.size() ; i++ ) {
      if (toChangeTicks.get(i).IntegerValue > 0) {
        toChangeTicks.get(i).IntegerValue = toChangeTicks.get(i).IntegerValue - 1;
      }
      if (toChangeTicks.get(i).IntegerValue == 0) {
      
      }
      else {
        toReturn.add(toChangeTicks.get(i));
      }
    }
    
    return toReturn;
  }
  private List<IntegerVector> processModifiersHelp2(List<IntegerVector> toChangeTicks) {
    List<IntegerVector> toReturn = new ArrayList(0);
    toReturn.clear();
    
    for ( int i = 0 ; i < toChangeTicks.size() ; i++ ) {
      if (toChangeTicks.get(i).IntegerValue > 0) {
        toChangeTicks.get(i).IntegerValue = toChangeTicks.get(i).IntegerValue - 1;
      }
      if (toChangeTicks.get(i).IntegerValue == 0) {
      
      }
      else {
        toReturn.add(toChangeTicks.get(i));
      }
    }
    
    return toReturn;
   }
  private void processModifiers() { //Reduce all Tick based modifiers by 1 Tick, remove the ones that reach 0; -1 means permanent; -2 means permanent until cleared
    
    //getPlayer().sendMessage("Invulnerable");
    if  (Invulnerable.BooleanValue) {  //If Invulnerable = true
      if (Invulnerable.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Invulnerable.IntegerValue = Invulnerable.IntegerValue -1;
      }     
      if (Invulnerable.IntegerValue == 0) {//if the timer expired
        Invulnerable.BooleanValue = false;  //set Invulnerable to false
      }
    }

    //getPlayer().sendMessage("Invulnerabilities");
    for ( int i = 0 ; i < DamageTypeCount ; i++) {
      if (Invulnerabilities[i].BooleanValue) {
        if (Invulnerabilities[i].IntegerValue > 0) {
          Invulnerabilities[i].IntegerValue = Invulnerabilities[i].IntegerValue - 1;
        }
        if (Invulnerabilities[i].IntegerValue == 0) {
          Invulnerabilities[i].BooleanValue = false;
        }
      }
    }
    
    
    //getPlayer().sendMessage("processModifiersHelp");
    MaxHealthIncreasesPreMult = processModifiersHelp(MaxHealthIncreasesPreMult);
    MaxHealthReductionsPreMult = processModifiersHelp(MaxHealthReductionsPreMult);
    MaxHealthMultipliers = processModifiersHelp(MaxHealthMultipliers);
    MaxHealthIncreasesPostMult = processModifiersHelp(MaxHealthIncreasesPostMult);
    MaxHealthReductionsPostMult = processModifiersHelp(MaxHealthReductionsPostMult);

    HealthRegenerationIncreasesPreMult = processModifiersHelp(HealthRegenerationIncreasesPreMult);
    HealthRegenerationReductionsPreMult = processModifiersHelp(HealthRegenerationReductionsPreMult);
    HealthRegenerationMultipliers = processModifiersHelp(HealthRegenerationMultipliers);
    HealthRegenerationIncreasesPostMult = processModifiersHelp(HealthRegenerationIncreasesPostMult);
    HealthRegenerationReductionsPostMult = processModifiersHelp(HealthRegenerationReductionsPostMult);

    for ( int i = 0 ; i<ResistanceIncreasesPreMult.length ; i++ ) {
      ResistanceIncreasesPreMult[i] = processModifiersHelp(ResistanceIncreasesPreMult[i]);
    }
    for ( int i = 0 ; i<ResistanceReductionsPreMult.length ; i++ ) {
      ResistanceReductionsPreMult[i] = processModifiersHelp(ResistanceReductionsPreMult[i]);
    }
    for ( int i = 0 ; i<ResistanceMultipliers.length ; i++ ) {
      ResistanceMultipliers[i] = processModifiersHelp(ResistanceMultipliers[i]);
    }
    for ( int i = 0 ; i<ResistanceIncreasesPostMult.length ; i++ ) {
      ResistanceIncreasesPostMult[i] = processModifiersHelp(ResistanceIncreasesPostMult[i]);
    }
    for ( int i = 0 ; i<ResistanceReductionsPostMult.length ; i++ ) {
      ResistanceReductionsPostMult[i] = processModifiersHelp(ResistanceReductionsPostMult[i]);
    }

    for ( int i = 0 ; i<TakenDamageIncreasesPreMult.length ; i++ ) {
      TakenDamageIncreasesPreMult[i] = processModifiersHelp(TakenDamageIncreasesPreMult[i]);
    }
    for ( int i = 0 ; i<TakenDamageReductionsPreMult.length ; i++ ) {
      TakenDamageReductionsPreMult[i] = processModifiersHelp(TakenDamageReductionsPreMult[i]);
    }
    for ( int i = 0 ; i<TakenDamageMultipliers.length ; i++ ) {
      TakenDamageMultipliers[i] = processModifiersHelp(TakenDamageMultipliers[i]);
    }
    for ( int i = 0 ; i<TakenDamageIncreasesPostMult.length ; i++ ) {
      TakenDamageIncreasesPostMult[i] = processModifiersHelp(TakenDamageIncreasesPostMult[i]);
    }
    for ( int i = 0 ; i<TakenDamageReductionsPostMult.length ; i++ ) {
      TakenDamageReductionsPostMult[i] = processModifiersHelp(TakenDamageReductionsPostMult[i]);
    }

    for ( int i = 0 ; i<InflictedDamageIncreasesPreMult.length ; i++ ) {
      InflictedDamageIncreasesPreMult[i] = processModifiersHelp(InflictedDamageIncreasesPreMult[i]);
    }
    for ( int i = 0 ; i<InflictedDamageReductionsPreMult.length ; i++ ) {
      InflictedDamageReductionsPreMult[i] = processModifiersHelp(InflictedDamageReductionsPreMult[i]);
    }
    for ( int i = 0 ; i<InflictedDamageMultipliers.length ; i++ ) {
      InflictedDamageMultipliers[i] = processModifiersHelp(InflictedDamageMultipliers[i]);
    }
    for ( int i = 0 ; i<InflictedDamageIncreasesPostMult.length ; i++ ) {
      InflictedDamageIncreasesPostMult[i] = processModifiersHelp(InflictedDamageIncreasesPostMult[i]);
    }
    for ( int i = 0 ; i<InflictedDamageReductionsPostMult.length ; i++ ) {
      InflictedDamageReductionsPostMult[i] = processModifiersHelp(InflictedDamageReductionsPostMult[i]);
    }

    for ( int i = 0 ; i<KnockbackTakenMultipliers.length ; i++ ) {
      KnockbackTakenMultipliers[i] = processModifiersHelp(KnockbackTakenMultipliers[i]);
    }
    for ( int i = 0 ; i<KnockbackTakenDirectionalMultipliers.length ; i++ ) {
      KnockbackTakenDirectionalMultipliers[i] = processModifiersHelp2(KnockbackTakenDirectionalMultipliers[i]);
    }

    for ( int i = 0 ; i<KnockbackDealedMultipliers.length ; i++ ) {
      KnockbackDealedMultipliers[i] = processModifiersHelp(KnockbackDealedMultipliers[i]);
    }
    for ( int i = 0 ; i<KnockbackDealedDirectionalMultipliers.length ; i++ ) {
      KnockbackDealedDirectionalMultipliers[i] = processModifiersHelp2(KnockbackDealedDirectionalMultipliers[i]);
    }
    
  }
  
  private void processFunctionBlockades() { //If Allow_... vars are set to false and the tick count is not negative, process their ticks!
  
    if  (! Allow_applyMaxHealthReduction.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_applyMaxHealthReduction.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_applyMaxHealthReduction.IntegerValue = Allow_applyMaxHealthReduction.IntegerValue -1;
      }     
      if (Allow_applyMaxHealthReduction.IntegerValue == 0) {//if the timer expired
        Allow_applyMaxHealthReduction.BooleanValue = true;  //allow the function again
      }
    }
    if  (! Allow_clearMaxHealthReductions.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_clearMaxHealthReductions.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_clearMaxHealthReductions.IntegerValue = Allow_clearMaxHealthReductions.IntegerValue -1;
      }     
      if (Allow_clearMaxHealthReductions.IntegerValue == 0) {//if the timer expired
        Allow_clearMaxHealthReductions.BooleanValue = true;  //allow the function again
      }
    }
 
    if  (! Allow_applyMaxHealthIncrease.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_applyMaxHealthIncrease.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_applyMaxHealthIncrease.IntegerValue = Allow_applyMaxHealthIncrease.IntegerValue -1;
      }     
      if (Allow_applyMaxHealthIncrease.IntegerValue == 0) {//if the timer expired
        Allow_applyMaxHealthIncrease.BooleanValue = true;  //allow the function again
      }
    }
    if  (! Allow_clearMaxHealthIncreases.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_clearMaxHealthIncreases.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_clearMaxHealthIncreases.IntegerValue = Allow_clearMaxHealthIncreases.IntegerValue -1;
      }     
      if (Allow_clearMaxHealthIncreases.IntegerValue      == 0) {//if the timer expired
        Allow_clearMaxHealthIncreases.BooleanValue = true;  //allow the function again
      }
    }
  
    if  (! Allow_applyMaxHealthMultiplier.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_applyMaxHealthMultiplier.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_applyMaxHealthMultiplier.IntegerValue = Allow_applyMaxHealthMultiplier.IntegerValue -1;
      }     
      if (Allow_applyMaxHealthMultiplier.IntegerValue == 0) {//if the timer expired
        Allow_applyMaxHealthMultiplier.BooleanValue = true;  //allow the function again
      }
    }
    if  (! Allow_clearMaxHealthMultipliers.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_clearMaxHealthMultipliers.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_clearMaxHealthMultipliers.IntegerValue = Allow_clearMaxHealthMultipliers.IntegerValue -1;
      }     
      if (Allow_clearMaxHealthMultipliers.IntegerValue == 0) {//if the timer expired
        Allow_clearMaxHealthMultipliers.BooleanValue = true;  //allow the function again
      }
    }
  
    if  (! Allow_applyHealthRegenerationReduction.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_applyHealthRegenerationReduction.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_applyHealthRegenerationReduction.IntegerValue = Allow_applyHealthRegenerationReduction.IntegerValue -1;
      }     
      if (Allow_applyHealthRegenerationReduction.IntegerValue == 0) {//if the timer expired
        Allow_applyHealthRegenerationReduction.BooleanValue = true;  //allow the function again
      }
    }
    if  (! Allow_clearHealthRegenerationReductions.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_clearHealthRegenerationReductions.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_clearHealthRegenerationReductions.IntegerValue = Allow_clearHealthRegenerationReductions.IntegerValue -1;
      }     
      if (Allow_clearHealthRegenerationReductions.IntegerValue == 0) {//if the timer expired
        Allow_clearHealthRegenerationReductions.BooleanValue = true;  //allow the function again
      }
    }

    if  (! Allow_applyHealthRegenerationIncrease.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_applyHealthRegenerationIncrease.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_applyHealthRegenerationIncrease.IntegerValue = Allow_applyHealthRegenerationIncrease.IntegerValue -1;
      }     
      if (Allow_applyHealthRegenerationIncrease.IntegerValue == 0) {//if the timer expired
        Allow_applyHealthRegenerationIncrease.BooleanValue = true;  //allow the function again
      }
    }
    if  (! Allow_clearHealthRegenerationIncreases.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_clearHealthRegenerationIncreases.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_clearHealthRegenerationIncreases.IntegerValue = Allow_clearHealthRegenerationIncreases.IntegerValue -1;
      }     
      if (Allow_clearHealthRegenerationIncreases.IntegerValue == 0) {//if the timer expired
        Allow_clearHealthRegenerationIncreases.BooleanValue = true;  //allow the function again
      }
    }
    
    if  (! Allow_applyHealthRegenerationReduction.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_applyHealthRegenerationReduction.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_applyHealthRegenerationReduction.IntegerValue = Allow_applyHealthRegenerationReduction.IntegerValue -1;
      }     
      if (Allow_applyHealthRegenerationReduction.IntegerValue == 0) {//if the timer expired
        Allow_applyHealthRegenerationReduction.BooleanValue = true;  //allow the function again
      }
    }
    if  (! Allow_clearHealthRegenerationReductions.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (Allow_clearHealthRegenerationReductions.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        Allow_clearHealthRegenerationReductions.IntegerValue = Allow_clearHealthRegenerationReductions.IntegerValue     -1;
      }     
      if (Allow_clearHealthRegenerationReductions.IntegerValue == 0) {//if the timer expired
        Allow_clearHealthRegenerationReductions.BooleanValue = true;  //allow the function again
      }
    }

    if  (! Allow_applyHealthRegenerationMultiplier.BooleanValue) {
      if (Allow_applyHealthRegenerationMultiplier.IntegerValue > 0) {
        Allow_applyHealthRegenerationMultiplier.IntegerValue = Allow_applyHealthRegenerationMultiplier.IntegerValue -1;
      }
      if (Allow_applyHealthRegenerationMultiplier.IntegerValue == 0) {
        Allow_applyHealthRegenerationMultiplier.BooleanValue = true;
      }
    }
    if  (! Allow_clearHealthRegenerationMultipliers.BooleanValue) {
      if (Allow_clearHealthRegenerationMultipliers.IntegerValue > 0) {
        Allow_clearHealthRegenerationMultipliers.IntegerValue = Allow_clearHealthRegenerationMultipliers.IntegerValue -1;
      }
      if (Allow_clearHealthRegenerationMultipliers.IntegerValue == 0) {
        Allow_clearHealthRegenerationMultipliers.BooleanValue = true;
      }
    }

    if  (! Allow_applyGeneralInvulnerability.BooleanValue) {
      if (Allow_applyGeneralInvulnerability.IntegerValue > 0) {
        Allow_applyGeneralInvulnerability.IntegerValue = Allow_applyGeneralInvulnerability.IntegerValue -1;
      }
      if (Allow_applyGeneralInvulnerability.IntegerValue == 0) {
        Allow_applyGeneralInvulnerability.BooleanValue = true;
      }
    }
    if  (! Allow_clearGeneralInvulnerability.BooleanValue) {
      if (Allow_clearGeneralInvulnerability.IntegerValue > 0) {
        Allow_clearGeneralInvulnerability.IntegerValue = Allow_clearGeneralInvulnerability.IntegerValue -1;
      }
      if (Allow_clearGeneralInvulnerability.IntegerValue == 0) {
        Allow_clearGeneralInvulnerability.BooleanValue = true;
      }
    }

    if  (! Allow_applyInvulnerability.BooleanValue) {
      if (Allow_applyInvulnerability.IntegerValue > 0) {
        Allow_applyInvulnerability.IntegerValue = Allow_applyInvulnerability.IntegerValue -1;
      }
      if (Allow_applyInvulnerability.IntegerValue == 0) {
        Allow_applyInvulnerability.BooleanValue = true;
      }
    }
    if  (! Allow_clearInvulnerabilities.BooleanValue) {
      if (Allow_clearInvulnerabilities.IntegerValue > 0) {
        Allow_clearInvulnerabilities.IntegerValue = Allow_clearInvulnerabilities.IntegerValue -1;
      }
      if (Allow_clearInvulnerabilities.IntegerValue == 0) {
        Allow_clearInvulnerabilities.BooleanValue = true;
      }
    }

  
    if  (! Allow_applyResistanceReduction.BooleanValue) {
      if (Allow_applyResistanceReduction.IntegerValue > 0) {
        Allow_applyResistanceReduction.IntegerValue = Allow_applyResistanceReduction.IntegerValue -1;
      }
      if (Allow_applyResistanceReduction.IntegerValue == 0) {
        Allow_applyResistanceReduction.BooleanValue = true;
      }
    }
    if  (! Allow_clearResistanceReductions.BooleanValue) {
      if (Allow_clearResistanceReductions.IntegerValue > 0) {
        Allow_clearResistanceReductions.IntegerValue = Allow_clearResistanceReductions.IntegerValue -1;
      }
      if (Allow_clearResistanceReductions.IntegerValue == 0) {
        Allow_clearResistanceReductions.BooleanValue = true;
      }
    }

    if  (! Allow_applyResistanceIncrease.BooleanValue) {
      if (Allow_applyResistanceIncrease.IntegerValue > 0) {
        Allow_applyResistanceIncrease.IntegerValue = Allow_applyResistanceIncrease.IntegerValue -1;
      }
      if (Allow_applyResistanceIncrease.IntegerValue == 0) {
        Allow_applyResistanceIncrease.BooleanValue = true;
      }
    }
    if  (! Allow_clearResistanceIncreases.BooleanValue) {
      if (Allow_clearResistanceIncreases.IntegerValue > 0) {
        Allow_clearResistanceIncreases.IntegerValue = Allow_clearResistanceIncreases.IntegerValue -1;
      }
      if (Allow_clearResistanceIncreases.IntegerValue == 0) {
        Allow_clearResistanceIncreases.BooleanValue = true;
      }
    }

    if  (! Allow_applyResistanceMultiplier.BooleanValue) {
      if (Allow_applyResistanceMultiplier.IntegerValue > 0) {
        Allow_applyResistanceMultiplier.IntegerValue = Allow_applyResistanceMultiplier.IntegerValue -1;
      }
      if (Allow_applyResistanceMultiplier.IntegerValue == 0) {
        Allow_applyResistanceMultiplier.BooleanValue = true;
      }
    }
    if  (! Allow_clearResistanceMultipliers.BooleanValue) {
      if (Allow_clearResistanceMultipliers.IntegerValue > 0) {
        Allow_clearResistanceMultipliers.IntegerValue = Allow_clearResistanceMultipliers.IntegerValue -1;
      }
      if (Allow_clearResistanceMultipliers.IntegerValue == 0) {
        Allow_clearResistanceMultipliers.BooleanValue = true;
      }
    }

    if  (! Allow_applyTakenDamageReduction.BooleanValue) {
      if (Allow_applyTakenDamageReduction.IntegerValue > 0) {
        Allow_applyTakenDamageReduction.IntegerValue = Allow_applyTakenDamageReduction.IntegerValue -1;
      }
      if (Allow_applyTakenDamageReduction.IntegerValue == 0) {
        Allow_applyTakenDamageReduction.BooleanValue = true;
      }
    }
    if  (! Allow_clearTakenDamageReductions.BooleanValue) {
      if (Allow_clearTakenDamageReductions.IntegerValue > 0) {
        Allow_clearTakenDamageReductions.IntegerValue = Allow_clearTakenDamageReductions.IntegerValue -1;
      }
      if (Allow_clearTakenDamageReductions.IntegerValue == 0) {
        Allow_clearTakenDamageReductions.BooleanValue = true;
      }
    }

    if  (! Allow_applyTakenDamageIncrease.BooleanValue) {
      if (Allow_applyTakenDamageIncrease.IntegerValue > 0) {
        Allow_applyTakenDamageIncrease.IntegerValue = Allow_applyTakenDamageIncrease.IntegerValue -1;
      }
      if (Allow_applyTakenDamageIncrease.IntegerValue == 0) {
        Allow_applyTakenDamageIncrease.BooleanValue = true;
      }
    }
    if  (! Allow_clearTakenDamageIncreases.BooleanValue) {
      if (Allow_clearTakenDamageIncreases.IntegerValue > 0) {
        Allow_clearTakenDamageIncreases.IntegerValue = Allow_clearTakenDamageIncreases.IntegerValue -1;
      }
      if (Allow_clearTakenDamageIncreases.IntegerValue == 0) {
        Allow_clearTakenDamageIncreases.BooleanValue = true;
      }
    }

    if  (! Allow_applyTakenDamageMultiplier.BooleanValue) {
      if (Allow_applyTakenDamageMultiplier.IntegerValue > 0) {
        Allow_applyTakenDamageMultiplier.IntegerValue = Allow_applyTakenDamageMultiplier.IntegerValue -1;
      }
      if (Allow_applyTakenDamageMultiplier.IntegerValue == 0) {
        Allow_applyTakenDamageMultiplier.BooleanValue = true;
      }
    }
    if  (! Allow_clearTakenDamageMultipliers.BooleanValue) {
      if (Allow_clearTakenDamageMultipliers.IntegerValue > 0) {
        Allow_clearTakenDamageMultipliers.IntegerValue = Allow_clearTakenDamageMultipliers.IntegerValue -1;
      }
      if (Allow_clearTakenDamageMultipliers.IntegerValue == 0) {
        Allow_clearTakenDamageMultipliers.BooleanValue = true;
      }
    }

    if  (! Allow_applyDealedDamageReduction.BooleanValue) {
      if (Allow_applyDealedDamageReduction.IntegerValue > 0) {
        Allow_applyDealedDamageReduction.IntegerValue = Allow_applyDealedDamageReduction.IntegerValue -1;
      }
      if (Allow_applyDealedDamageReduction.IntegerValue == 0) {
        Allow_applyDealedDamageReduction.BooleanValue = true;
      }
    }
    if  (! Allow_clearDealedDamageReductions.BooleanValue) {
      if (Allow_clearDealedDamageReductions.IntegerValue > 0) {
        Allow_clearDealedDamageReductions.IntegerValue = Allow_clearDealedDamageReductions.IntegerValue -1;
      }
      if (Allow_clearDealedDamageReductions.IntegerValue      == 0) {
        Allow_clearDealedDamageReductions.BooleanValue = true;
      }
    }

    if  (! Allow_applyDealedDamageIncrease.BooleanValue) {
      if (Allow_applyDealedDamageIncrease.IntegerValue > 0) {
        Allow_applyDealedDamageIncrease.IntegerValue = Allow_applyDealedDamageIncrease.IntegerValue -1;
      }
      if (Allow_applyDealedDamageIncrease.IntegerValue == 0) {
        Allow_applyDealedDamageIncrease.BooleanValue = true;
      }
    }
    if  (! Allow_clearDealedDamageIncreases.BooleanValue) {
      if (Allow_clearDealedDamageIncreases.IntegerValue > 0) {
        Allow_clearDealedDamageIncreases.IntegerValue = Allow_clearDealedDamageIncreases.IntegerValue -1;
      }
      if (Allow_clearDealedDamageIncreases.IntegerValue == 0) {
        Allow_clearDealedDamageIncreases.BooleanValue = true;
      }
    }

    if  (! Allow_applyDealedDamageMultiplier.BooleanValue) {
      if (Allow_applyDealedDamageMultiplier.IntegerValue > 0) {
        Allow_applyDealedDamageMultiplier.IntegerValue = Allow_applyDealedDamageMultiplier.IntegerValue -1;
      }
      if (Allow_applyDealedDamageMultiplier.IntegerValue == 0) {
        Allow_applyDealedDamageMultiplier.BooleanValue = true;
      }
    }
    if  (! Allow_clearDealedDamageMultipliers.BooleanValue) {
      if (Allow_clearDealedDamageMultipliers.IntegerValue > 0) {
        Allow_clearDealedDamageMultipliers.IntegerValue = Allow_clearDealedDamageMultipliers.IntegerValue -1;
      }
      if (Allow_clearDealedDamageMultipliers.IntegerValue == 0) {
        Allow_clearDealedDamageMultipliers.BooleanValue = true;
      }
    }

    if  (! Allow_applyKnockbackTakenMultiplier.BooleanValue) {
      if (Allow_applyKnockbackTakenMultiplier.IntegerValue > 0) {
        Allow_applyKnockbackTakenMultiplier.IntegerValue = Allow_applyKnockbackTakenMultiplier.IntegerValue -1;
      }
      if (Allow_applyKnockbackTakenMultiplier.IntegerValue == 0) {
        Allow_applyKnockbackTakenMultiplier.BooleanValue = true;
      }
    }
    if  (! Allow_clearKnockbackTakenMultipliers.BooleanValue) {
      if (Allow_clearKnockbackTakenMultipliers.IntegerValue > 0) {
        Allow_clearKnockbackTakenMultipliers.IntegerValue = Allow_clearKnockbackTakenMultipliers.IntegerValue -1;
      }
      if (Allow_clearKnockbackTakenMultipliers.IntegerValue == 0) {
        Allow_clearKnockbackTakenMultipliers.BooleanValue = true;
      }
    }

    if  (! Allow_applyKnockbackTakenDirectionalMultiplier.BooleanValue) {
      if (Allow_applyKnockbackTakenDirectionalMultiplier.IntegerValue > 0) {
        Allow_applyKnockbackTakenDirectionalMultiplier.IntegerValue = Allow_applyKnockbackTakenDirectionalMultiplier.IntegerValue -1;
      }
      if (Allow_applyKnockbackTakenDirectionalMultiplier.IntegerValue == 0) {
        Allow_applyKnockbackTakenDirectionalMultiplier.BooleanValue = true;
      }
    }
    if  (! Allow_clearKnockbackTakenDirectionalMultipliers.BooleanValue) {
      if (Allow_clearKnockbackTakenDirectionalMultipliers.IntegerValue > 0) {
        Allow_clearKnockbackTakenDirectionalMultipliers.IntegerValue = Allow_clearKnockbackTakenDirectionalMultipliers.IntegerValue -1;
      }
      if (Allow_clearKnockbackTakenDirectionalMultipliers.IntegerValue == 0) {
        Allow_clearKnockbackTakenDirectionalMultipliers.BooleanValue = true;
      }
    }

    if  (! Allow_applyKnockbackDealedDirectionalMultiplier.BooleanValue) {
      if (Allow_applyKnockbackDealedDirectionalMultiplier.IntegerValue > 0) {
        Allow_applyKnockbackDealedDirectionalMultiplier.IntegerValue = Allow_applyKnockbackDealedDirectionalMultiplier.IntegerValue -1;
      }
      if (Allow_applyKnockbackDealedDirectionalMultiplier.IntegerValue == 0) {
        Allow_applyKnockbackDealedDirectionalMultiplier.BooleanValue = true;
      }
    }
    if  (! Allow_clearKnockbackDealedMultipliers.BooleanValue) {
      if (Allow_clearKnockbackDealedMultipliers.IntegerValue > 0) {
        Allow_clearKnockbackDealedMultipliers.IntegerValue = Allow_clearKnockbackDealedMultipliers.IntegerValue -1;
      }
      if (Allow_clearKnockbackDealedMultipliers.IntegerValue == 0) {
        Allow_clearKnockbackDealedMultipliers.BooleanValue = true;
      }
    }

    if  (! Allow_applyKnockbackDealedDirectionalMultiplier.BooleanValue) {
      if (Allow_applyKnockbackDealedDirectionalMultiplier.IntegerValue > 0) {
        Allow_applyKnockbackDealedDirectionalMultiplier.IntegerValue = Allow_applyKnockbackDealedDirectionalMultiplier.IntegerValue -1;
      }
      if (Allow_applyKnockbackDealedDirectionalMultiplier.IntegerValue == 0) {
        Allow_applyKnockbackDealedDirectionalMultiplier.BooleanValue = true;
      }
    }
    if  (! Allow_clearKnockbackDealedDirectionalMultipliers.BooleanValue) {
      if (Allow_clearKnockbackDealedDirectionalMultipliers.IntegerValue > 0) {
        Allow_clearKnockbackDealedDirectionalMultipliers.IntegerValue = Allow_clearKnockbackDealedDirectionalMultipliers.IntegerValue -1;
      }
      if (Allow_clearKnockbackDealedDirectionalMultipliers.IntegerValue == 0) {
        Allow_clearKnockbackDealedDirectionalMultipliers.BooleanValue = true;
      }
    }
    
    
    if  (! Allow_EffectRemove.BooleanValue) {
      if (Allow_EffectRemove.IntegerValue > 0) {
        Allow_EffectRemove.IntegerValue = Allow_EffectRemove.IntegerValue -1;
      }
      if (Allow_EffectRemove.IntegerValue == 0) {
        Allow_EffectRemove.BooleanValue = true;
      }
    }
    if  (! Allow_EffectExists.BooleanValue) {
      if (Allow_EffectExists.IntegerValue > 0) {
        Allow_EffectExists.IntegerValue = Allow_EffectExists.IntegerValue -1;
      }
      if (Allow_EffectExists.IntegerValue == 0) {
        Allow_EffectExists.BooleanValue = true;
      }
    }
    if  (! Allow_EffectApply.BooleanValue) {
      if (Allow_EffectApply.IntegerValue > 0) {
        Allow_EffectApply.IntegerValue = Allow_EffectApply.IntegerValue -1;
      }
      if (Allow_EffectApply.IntegerValue == 0) {
        Allow_EffectApply.BooleanValue = true;
      }
    }

}
  
  private Double calculateHealthRegeneration() {
    Double ToReturn = HealthRegenerationPerTick;

    for ( int i= 0 ;  i < HealthRegenerationIncreasesPreMult.size() ; i++ ) {
      ToReturn = ToReturn + HealthRegenerationIncreasesPreMult.get(i).DoubleValue;
    }
    for ( int i= 0 ;  i < HealthRegenerationReductionsPreMult.size() ; i++ ) {
      ToReturn = ToReturn - HealthRegenerationReductionsPreMult.get(i).DoubleValue;
    }
    for ( int i= 0 ;  i < HealthRegenerationMultipliers.size() ; i++ ) {
      ToReturn = ToReturn * HealthRegenerationMultipliers.get(i).DoubleValue;
    }
    for ( int i= 0 ;  i < HealthRegenerationIncreasesPostMult.size() ; i++ ) {
      ToReturn = ToReturn + HealthRegenerationIncreasesPostMult.get(i).DoubleValue;
    }
    for ( int i= 0 ;  i < HealthRegenerationReductionsPostMult.size() ; i++ ) {
      ToReturn = ToReturn - HealthRegenerationReductionsPostMult.get(i).DoubleValue;
    }

    return ToReturn;    
  }
  private void regenerate() {  //called by tick before refresh
    if (AllowRegeneration.BooleanValue) {
      IncreaseHealth(calculateHealthRegeneration());
    }
  }
  private int calculateHealthToBeShown() {
    int ToReturn = (int)Math.round(20*(Health/MaxHealth));
    if (ToReturn == 0) {
      ToReturn = 1;
    }
    return ToReturn;
  }
  private void refreshHealth() { //called by refresh
  //TODO: refresh the Players health bar(using Health and MaxHealth)
    getPlayer().setHealth(calculateHealthToBeShown());
  }
  private void refreshHunger() { //called by refresh
  //Do nothing for now, until the Hunger bar gets a usage
  }
  private void refresh() { //refreshes Health Bar/Hunger Bar/etc  is called in tick
    refreshHealth();
    refreshHunger();
  }
  
  private void tick() { //Is called every tick
    
    regenerate();
    processModifiers();
    processFunctionBlockades();
    //DEBUG getPlayer().sendMessage("Tick");
    refresh();
    //DO NOT code anything behind refresh despite there is a good reason to do so!
  }

  
  private void onTouchingGround() {
      
  }
  protected void spoofTouchingGround() {
      
  }

  protected Boolean addMaxHealthReductionMult(SuperSmashKit by, Double amount, int DurationTicks, Boolean preMult) {
    if (Allow_applyMaxHealthReduction.BooleanValue) {
        if (preMult) {
          MaxHealthReductionsPreMult.add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          MaxHealthReductionsPostMult.add(new IntegerDouble(DurationTicks, amount));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearMaxHealthReductions(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearMaxHealthReductions.BooleanValue) {
      if (preMult) {
        MaxHealthReductionsPreMult.clear();
      }
      else {
        MaxHealthReductionsPostMult.clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addMaxHealthIncrease(SuperSmashKit by, Double amount, int DurationTicks, Boolean preMult){
    if (Allow_applyMaxHealthIncrease.BooleanValue) {
        if (preMult) {
          MaxHealthIncreasesPreMult.add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          MaxHealthIncreasesPostMult.add(new IntegerDouble(DurationTicks, amount));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearMaxHealthIncreases(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
      if (Allow_clearMaxHealthIncreases.BooleanValue) {
      if (preMult) {
        MaxHealthIncreasesPreMult.clear();
      }
      else {
        MaxHealthIncreasesPostMult.clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addMaxHealthMultiplier(SuperSmashKit by, Double amount, int DurationTicks) {
    if (Allow_applyMaxHealthMultiplier.BooleanValue) {
      MaxHealthMultipliers.add(new IntegerDouble(DurationTicks, amount));
      return true;
    }
    else {
      return false; 
    }  
  }
  protected Boolean clearMaxHealthMultipliers() { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearMaxHealthMultipliers.BooleanValue) {
      MaxHealthMultipliers.clear();
      return true;
    }
    else {
      return false; 
    }  
  }

  protected Boolean addGeneralInvulnerability(int DurationTicks) {
   if (Allow_applyGeneralInvulnerability.BooleanValue && (DurationTicks > 0)) {
      if (Invulnerable.IntegerValue > DurationTicks) {
         Invulnerable.IntegerValue = DurationTicks;
      }   
      Invulnerable.BooleanValue = true;
      return false;
    }
    else {
      return false;
    }
  }
  protected Boolean clearGeneralInvulnerability() { //-1 Ticks shouldnt be cleared but currently are
   if (Allow_clearGeneralInvulnerability.BooleanValue) {
      Invulnerable.IntegerValue = 0;
      Invulnerable.BooleanValue = true;
      return false;
    }
    else {
      return false;
    }
  }

  protected Boolean addInvulnerability(SuperSmashKit by, String DamageType, int DurationTicks) {
    return addInvulnerability(by, stringDamageTypeToInt(DamageType), DurationTicks);
  }
  protected Boolean addInvulnerability(SuperSmashKit by, int DamageType, int DurationTicks) {
  if (Allow_applyInvulnerability.BooleanValue && (DurationTicks > 0)) {
      if (Invulnerabilities[DamageType].IntegerValue > DurationTicks) {
         Invulnerabilities[DamageType].IntegerValue = DurationTicks;
      }   
      Invulnerabilities[DamageType].BooleanValue = true;
      return false;
    }
    else {
      return false;
    }
   }  
  protected Boolean clearInvulnerability(String DamageType) {
    return clearInvulnerability(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearInvulnerability(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
  if (Allow_clearInvulnerabilities.BooleanValue) {
      Invulnerabilities[DamageType].IntegerValue = 0;
      Invulnerabilities[DamageType].BooleanValue = true;
      return false;
    }
    else {
      return false;
    }
   }

  protected Boolean addHealthRegenerationReduction(SuperSmashKit by, Double amountPerSecond, int DurationTicks, Boolean preMult) {
    if (Allow_applyHealthRegenerationReduction.BooleanValue) {
      if (preMult) {
        HealthRegenerationReductionsPreMult.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
      }
      else {
        HealthRegenerationReductionsPostMult.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
      }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearHealthRegenerationReductions(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearHealthRegenerationReductions.BooleanValue) {
      if (preMult) {
        HealthRegenerationReductionsPreMult.clear();
      }
      else {
        HealthRegenerationReductionsPostMult.clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addHealthRegenerationIncrease(SuperSmashKit by, Double amountPerSecond, int DurationTicks, Boolean preMult) {
      if (Allow_applyHealthRegenerationIncrease.BooleanValue) {
        if (preMult) {
          HealthRegenerationIncreasesPreMult.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
        }
        else {
          HealthRegenerationIncreasesPostMult.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearHealthRegenerationIncreases(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearHealthRegenerationIncreases.BooleanValue) {
      if (preMult) {
        HealthRegenerationIncreasesPreMult.clear();
      }
      else {
        HealthRegenerationIncreasesPostMult.clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addHealthRegenerationMultiplier(SuperSmashKit by, Double amountPerSecond, int DurationTicks) {
    if (Allow_applyHealthRegenerationMultiplier.BooleanValue) {
      HealthRegenerationMultipliers.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
      return true;
    }
    else {
      return false; 
    }  
  }
  protected Boolean clearHealthRegenerationMultipliers() { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearHealthRegenerationMultipliers.BooleanValue) {
      HealthRegenerationMultipliers.clear();
      return true;
    }
    else {
      return false; 
    }      
  }

  protected Boolean addResistanceReduction(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
    return addResistanceReduction(by, stringDamageTypeToInt(DamageType), amount, DurationTicks, preMult);
  }
  protected Boolean addResistanceReduction(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
    if (Allow_applyResistanceReduction.BooleanValue) {
      if (preMult) {
        ResistanceReductionsPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
      }
      else {
        ResistanceReductionsPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
      }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearResistanceReductions(String DamageType, Boolean preMult) { 
    return clearResistanceReductions(stringDamageTypeToInt(DamageType), preMult);
  }
  protected Boolean clearResistanceReductions(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearResistanceReductions.BooleanValue) {
      if (preMult) {
        ResistanceReductionsPreMult[DamageType].clear();
      }
      else {
        ResistanceReductionsPostMult[DamageType].clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addResistanceIncrease(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
    return addResistanceIncrease(by, stringDamageTypeToInt(DamageType), amount, DurationTicks, preMult);
  }
  protected Boolean addResistanceIncrease(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
    if (Allow_applyResistanceIncrease.BooleanValue) {
        if (preMult) {
          ResistanceIncreasesPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          ResistanceIncreasesPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearResistanceIncreases(String DamageType, Boolean preMult) {
    return clearResistanceIncreases(stringDamageTypeToInt(DamageType), preMult);
  }
  protected Boolean clearResistanceIncreases(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearResistanceIncreases.BooleanValue) {
      if (preMult) {
        ResistanceIncreasesPreMult[DamageType].clear();
      }
      else {
        ResistanceIncreasesPostMult[DamageType].clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addResistanceMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
    return addResistanceMultiplier(by, stringDamageTypeToInt(DamageType), amount, DurationTicks);
  }
  protected Boolean addResistanceMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
    if (Allow_applyResistanceMultiplier.BooleanValue) {
      ResistanceMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return true;
    }
    else {
      return false; 
    }    }
  protected Boolean clearResistanceMultipliers(String DamageType) {
    return clearResistanceMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearResistanceMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearResistanceMultipliers.BooleanValue) {
      ResistanceMultipliers[DamageType].clear();
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean disallowHealing(SuperSmashKit by, int DurationTicks) {
    if (AllowHealing.BooleanValue) {
      AllowHealing.BooleanValue = false;
      AllowHealing.IntegerValue = DurationTicks;
    }
    else {
      if (AllowHealing.IntegerValue < DurationTicks) {
        AllowHealing.IntegerValue = DurationTicks;
      }
    }
    
    return true;
  }

  protected Boolean disableRegeneration(SuperSmashKit by, int DurationTicks ) {
    if (AllowRegeneration.BooleanValue) {
      AllowRegeneration.BooleanValue = false;
      AllowRegeneration.IntegerValue = DurationTicks;
    }
    else {
      if (AllowRegeneration.IntegerValue < DurationTicks) {
        AllowRegeneration.IntegerValue = DurationTicks;
      }
    }
    
    return true;
  }
  
  protected Boolean addTakenDamageReduction(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
    return addTakenDamageReduction(by, stringDamageTypeToInt(DamageType), amount, DurationTicks, preMult);
  }
  protected Boolean addTakenDamageReduction(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
      if (Allow_applyTakenDamageReduction.BooleanValue) {
        if (preMult) {
          TakenDamageReductionsPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          TakenDamageReductionsPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearTakenDamageReductions(String DamageType, Boolean preMult) {
    return clearTakenDamageReductions(stringDamageTypeToInt(DamageType), preMult);
  }
  protected Boolean clearTakenDamageReductions(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearTakenDamageReductions.BooleanValue) {
      if (preMult) {
        TakenDamageReductionsPreMult[DamageType].clear();
      }
      else {
        TakenDamageReductionsPostMult[DamageType].clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addTakenDamageIncrease(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
    return addTakenDamageIncrease(by, stringDamageTypeToInt(DamageType), amount, DurationTicks, preMult);
  }
  protected Boolean addTakenDamageIncrease(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
    if (Allow_applyTakenDamageIncrease.BooleanValue) {
      if (preMult) {
        TakenDamageIncreasesPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
      }
      else {
        TakenDamageIncreasesPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
      }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearTakenDamageIncreases(String DamageType, Boolean preMult) {
    return clearTakenDamageIncreases(stringDamageTypeToInt(DamageType), preMult);
  }
  protected Boolean clearTakenDamageIncreases(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearTakenDamageIncreases.BooleanValue) {
      if (preMult) {
        TakenDamageIncreasesPreMult[DamageType].clear();
      }
      else {
        TakenDamageIncreasesPostMult[DamageType].clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addTakenDamageMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
    return addTakenDamageMultiplier(by, stringDamageTypeToInt(DamageType), amount, DurationTicks);
  }
  protected Boolean addTakenDamageMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
    if (Allow_applyTakenDamageMultiplier.BooleanValue) {
      TakenDamageMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return true;
    }
    else {
      return false; 
    }    
  }
  protected Boolean clearTakenDamageMultiplier(String DamageType) {
    return clearTakenDamageMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearTakenDamageMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearTakenDamageMultipliers.BooleanValue) {
      TakenDamageMultipliers[DamageType].clear();
      return true;
    }
    else {
      return false; 
    }    
  }

  protected Boolean addInflictedDamageReduction(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
    return addInflictedDamageReduction(by, stringDamageTypeToInt(DamageType), amount, DurationTicks, preMult);
  }
  protected Boolean addInflictedDamageReduction(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
    if (Allow_applyDealedDamageReduction.BooleanValue) {
        if (preMult) {
          InflictedDamageReductionsPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          InflictedDamageReductionsPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearInflictedDamageReductions(String DamageType, Boolean preMult) {
    return clearInflictedDamageReductions(stringDamageTypeToInt(DamageType), preMult);
  }
  protected Boolean clearInflictedDamageReductions(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
      if (Allow_clearDealedDamageReductions.BooleanValue) {
      if (preMult) {
        InflictedDamageReductionsPreMult[DamageType].clear();
      }
      else {
        InflictedDamageReductionsPostMult[DamageType].clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addInflictedDamageIncrease(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
    return addInflictedDamageIncrease(by, stringDamageTypeToInt(DamageType), amount, DurationTicks, preMult);
  }
  protected Boolean addInflictedDamageIncrease(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
    if (Allow_applyDealedDamageIncrease.BooleanValue) {
        if (preMult) {
          InflictedDamageIncreasesPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          InflictedDamageIncreasesPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearInflictedDamageIncreases(String DamageType, Boolean preMult) {
    return clearInflictedDamageIncreases(stringDamageTypeToInt(DamageType), preMult);
  }
  protected Boolean clearInflictedDamageIncreases(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearDealedDamageIncreases.BooleanValue) {
      if (preMult) {
        InflictedDamageIncreasesPreMult[DamageType].clear();
      }
      else {
        InflictedDamageIncreasesPostMult[DamageType].clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addInflictedDamageMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
    return addInflictedDamageMultiplier(by, DamageType, amount, DurationTicks);
  }
  protected Boolean addInflictedDamageMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
    if (Allow_applyDealedDamageMultiplier.BooleanValue) {
      InflictedDamageMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearInflictedDamageMultipliers(String DamageType) {
    return clearInflictedDamageMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearInflictedDamageMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearDealedDamageMultipliers.BooleanValue) {
      InflictedDamageMultipliers[DamageType].clear();
      return true;
    }
    else {
      return false; 
    }
  }


  protected Boolean addKnockbackTakenMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
    return addKnockbackTakenMultiplier(by, stringDamageTypeToInt(DamageType), amount, DurationTicks);
  }
  protected Boolean addKnockbackTakenMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
    if (Allow_applyKnockbackTakenMultiplier.BooleanValue) {
      KnockbackTakenMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return false;
    }
    else {
      return false;
    }
  }
  protected Boolean clearKnockbackTakenMultipliers(String DamageType) {
    return clearKnockbackTakenMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearKnockbackTakenMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
    if (Allow_clearKnockbackTakenMultipliers.BooleanValue) {
      KnockbackTakenMultipliers[DamageType].clear();
      return false;
    }
    else {
      return false;
    }
  }

  protected Boolean addKnockbackTakenDirectionalMultiplier(SuperSmashKit by, String DamageType, Vector multiplier, int DurationTicks) {
    return addKnockbackTakenDirectionalMultiplier(by, stringDamageTypeToInt(DamageType), multiplier, DurationTicks);
  }
  protected Boolean addKnockbackTakenDirectionalMultiplier(SuperSmashKit by, int DamageType, Vector multiplier, int DurationTicks) {
    if (Allow_applyKnockbackTakenDirectionalMultiplier.BooleanValue) {
      KnockbackDealedDirectionalMultipliers[DamageType].add(new IntegerVector(DurationTicks, multiplier.clone()));
      return false;
    }
    else {
      return false;
    }
  }
  protected Boolean clearKnockbackTakenDirectionalMultipliers(String DamageType) {
    return clearKnockbackTakenDirectionalMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearKnockbackTakenDirectionalMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
   if (Allow_clearKnockbackTakenDirectionalMultipliers.BooleanValue) {
      KnockbackTakenDirectionalMultipliers[DamageType].clear();
      return false;
    }
    else {
      return false;
    }
   }

  protected Boolean addKnockbackDealedMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
    return addKnockbackDealedMultiplier(by, stringDamageTypeToInt(DamageType), amount, DurationTicks);
  }
  protected Boolean addKnockbackDealedMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
    if (Allow_applyKnockbackDealedMultiplier.BooleanValue) {
      KnockbackDealedMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return false;
    }
    else {
      return false;
    }
  }
  protected Boolean clearKnockbackDealedMultipliers(String DamageType) {
    return clearKnockbackDealedMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearKnockbackDealedMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
   if (Allow_clearKnockbackDealedMultipliers.BooleanValue) {
      KnockbackDealedDirectionalMultipliers[DamageType].clear();
      return false;
    }
    else {
      return false;
    }
  }

  protected Boolean addKnockbackDealedDirectionalMultiplier(SuperSmashKit by, String DamageType, Vector multiplier, int DurationTicks) {
    return addKnockbackDealedDirectionalMultiplier(by, stringDamageTypeToInt(DamageType), multiplier, DurationTicks);
  }
  protected Boolean addKnockbackDealedDirectionalMultiplier(SuperSmashKit by, int DamageType, Vector multiplier, int DurationTicks) {
   if (Allow_applyKnockbackDealedDirectionalMultiplier.BooleanValue) {
      KnockbackDealedDirectionalMultipliers[DamageType].add(new IntegerVector(DurationTicks, multiplier.clone()));
      return false;
    }
    else {
      return false;
    }
   }
  protected Boolean clearKnockbackDealedDirectionalMultipliers(String DamageType) {
    return clearKnockbackDealedDirectionalMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearKnockbackDealedDirectionalMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
   if (Allow_clearKnockbackDealedDirectionalMultipliers.BooleanValue) {
      KnockbackDealedDirectionalMultipliers[DamageType].clear();
      return false;
    }
    else {
      return false;
    }
   }


  protected Boolean effectRemove(String EffectName) {
    return false;  
  }
  protected Boolean effectRemove(String EffectName, SuperSmashKit by) { 
    return false;
  }
  protected Boolean effectExists(String EffectName) {
    return false;
  }
  protected Boolean effectExists(String EffectName, SuperSmashKit by) { 
    return false;
  }
  protected Boolean effectApply(String EffectName, SuperSmashKit by) {
    return false;
  }


  public String[] createBaseDescription() { //Creates a description, based on the const values, abilitys can be added by the specific kit
    String[] ToReturn = new String[6];
  
    ToReturn[0] = "Health: " + Double.toString(MaxHealth);
    ToReturn[1] = "Regeneration per sec: " + Double.toString(Math.round(HealthRegenerationPerTick * 20 * 10)/10.0);
    ToReturn[2] = "Damage: " + Double.toString(AttackDamage);
    ToReturn[3] = "Armor: " + Double.toString(Resistances[1]);
    ToReturn[4] = "KnockbackDealed: " + Double.toString(KnockbackDealed);
    ToReturn[5] = "KnockbackTaken: " + Double.toString(KnockbackTaken);
    
    getPlayer().sendMessage(ToReturn[0]);
    
    return ToReturn;
  }
  
}
