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
        call protected void init(Double initmaxHealth, Double inithealthRegenerationPerTick, Double initattackDamage, Double initknockbackDealed, Double initknockbackTaken, Double[] initresistances){...}
        with the input beeing the base stats of the kit, from there the protected function (see 2.) should be able to handle any damage/knockback inflictions as well as stat modifiers!
       -to stop the background system which is working based on a BukkitRunnable, be sure to call preDekit(); before dekitting since otherwise the Runnable will keep going every tick -
        this should already be coded in SuperSmashController.dekit() {... playerKits.get(player).preDekit(); ...} so its automatically happening on dekitting
        
    2. Handling Damage and Knockback as well as their modifiers:
       -2.1: General Functions and Inflicting Damage/Knockback:
           +DamageTypes:
             -protected int getdamageTypeCount() //returns the amount of Damage Types
             -protected int stringDamageTypeToInt(String DamageType) //Convert a String DamageType into the connected int; returns -1 if invalid
             -protected String intDamageTypeToString(int DamageType) //Convert an int DamageType into the connected String; returns "None" if invalid; not needed, mainly used so in the kit the string representation can be used in overridable methods(see 2.3)
           +Damage System
             -protected Double gethealth() //returns the current health, not to be confused with maxHealth
             -protected Double gethealthPercentage() //returns the percentage of health compared to maxHealth

             -protected Double calculateResistance(int DamageType) //can be used to check the total Resistance considering a DamageType
             -protected Double calculateResistance(String DamageType) //can be used to check the total Resistance considering a DamageType

             -protected Double heal(Double amount) //increases your health
             -protected void inflictSelfDamage(Double amount) //reduce your own health

             //if the damage type is melee and the amount is -1 it will grab the provided melee damage (attackDamage)
             -protected Double inflictDamage(SuperSmashKit to, int DamageType, Double amount, String info)  //Info can be empty or can contain additions, for example an ability name
             -protected Double inflictDamage(SuperSmashKit to, String DamageType, Double amount, String info) //This will call the first one, but is more smooth to read(for example "True" instead of 0 as DamageType); Info can be empty or can contain additions, for example an ability name

           +Knockback
             -protected Vector inflictKnockback(SuperSmashKit to, String DamageType, Vector v, String info)  //Info can be empty or can contain additions, for example an ability name
             -protected Vector inflictKnockback(SuperSmashKit to, int DamageType, Vector v, String info)  //Info can be empty or can contain additions, for example an ability name
             -protected void applySelfVelocity(Vector v) //Info can be empty or can contain additions, for example an ability name

           +Other
             protected void spoofTouchingGround() // not coded yet

       -2.2: Adding/clearing Modifiers:
           +Modifiers are used to change a value of your choice by +/- x.x or multiply them by a multiplier of your choice.
           +There can be multiple modifiers at once of the same type!

            protected Boolean addmaxHealthReductionMult(SuperSmashKit by, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean clearmaxHealthReductions(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addmaxHealthIncrease(SuperSmashKit by, Double amount, int DurationTicks, Boolean preMult){
            protected Boolean clearmaxHealthIncreases(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addmaxHealthMultiplier(SuperSmashKit by, Double amount, int DurationTicks) {
            protected Boolean clearmaxHealthMultipliers() { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addGeneralInvulnerability(int DurationTicks) {
            protected Boolean clearGeneralInvulnerability() { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addInvulnerability(SuperSmashKit by, String DamageType, int DurationTicks) {
            protected Boolean addInvulnerability(SuperSmashKit by, int DamageType, int DurationTicks) {
            protected Boolean clearInvulnerability(String DamageType) {
            protected Boolean clearInvulnerability(int DamageType) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addhealthRegenerationReduction(SuperSmashKit by, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean clearhealthRegenerationReductions(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addhealthRegenerationIncrease(SuperSmashKit by, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean clearhealthRegenerationIncreases(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addhealthRegenerationMultiplier(SuperSmashKit by, Double amount, int DurationTicks) {
            protected Boolean clearhealthRegenerationMultipliers() { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addResistanceReduction(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean addResistanceReduction(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean clearResistanceReductions(String DamageType, Boolean preMult) {
            protected Boolean clearResistanceReductions(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addResistanceIncrease(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean addResistanceIncrease(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean clearResistanceIncreases(String DamageType, Boolean preMult) {
            protected Boolean clearResistanceIncreases(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addResistanceMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
            protected Boolean addResistanceMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
            protected Boolean clearresistanceMultipliers(String DamageType) {
            protected Boolean clearresistanceMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean disallowHealing(SuperSmashKit by, int DurationTicks) {
            protected Boolean disableRegeneration(SuperSmashKit by, int DurationTicks ) {

            protected Boolean addTakenDamageReduction(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean addTakenDamageReduction(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean clearTakenDamageReductions(String DamageType, Boolean preMult) {
            protected Boolean clearTakenDamageReductions(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addTakenDamageIncrease(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean addTakenDamageIncrease(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean clearTakenDamageIncreases(String DamageType, Boolean preMult) {
            protected Boolean clearTakenDamageIncreases(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addTakenDamageMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
            protected Boolean addTakenDamageMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
            protected Boolean clearTakenDamageMultiplier(String DamageType) {
            protected Boolean cleartakenDamageMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addInflictedDamageReduction(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean addInflictedDamageReduction(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean clearInflictedDamageReductions(String DamageType, Boolean preMult) {
            protected Boolean clearInflictedDamageReductions(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addInflictedDamageIncrease(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean addInflictedDamageIncrease(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
            protected Boolean clearInflictedDamageIncreases(String DamageType, Boolean preMult) {
            protected Boolean clearInflictedDamageIncreases(int DamageType, Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addInflictedDamageMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
            protected Boolean addInflictedDamageMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
            protected Boolean clearinflictedDamageMultipliers(String DamageType) {
            protected Boolean clearinflictedDamageMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addknockbackTakenMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
            protected Boolean addknockbackTakenMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
            protected Boolean clearknockbackTakenMultipliers(String DamageType) {
            protected Boolean clearknockbackTakenMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addknockbackTakenDirectionalMultiplier(SuperSmashKit by, String DamageType, Vector multiplier, int DurationTicks) {
            protected Boolean addknockbackTakenDirectionalMultiplier(SuperSmashKit by, int DamageType, Vector multiplier, int DurationTicks) {
            protected Boolean clearknockbackTakenDirectionalMultipliers(String DamageType) {
            protected Boolean clearknockbackTakenDirectionalMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addknockbackDealedMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
            protected Boolean addknockbackDealedMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
            protected Boolean clearknockbackDealedMultipliers(String DamageType) {
            protected Boolean clearknockbackDealedMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are

            protected Boolean addknockbackDealedDirectionalMultiplier(SuperSmashKit by, String DamageType, Vector multiplier, int DurationTicks) {
            protected Boolean addknockbackDealedDirectionalMultiplier(SuperSmashKit by, int DamageType, Vector multiplier, int DurationTicks) {
            protected Boolean clearknockbackDealedDirectionalMultipliers(String DamageType) {
            protected Boolean clearknockbackDealedDirectionalMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are

       -2.3: Overwritable Functions for the actual kits:
             1.method      : manipulateDamageTaking is called before calling reducehealth by takeDamage and represent the damage that was already merged with the proper Resistance
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
  
             5.method      : afterLosinghealth is called by takeDamage after it called reducehealth
               declaration : public void afterLosinghealth(SuperSmashKit by, String DamageType, Double losthealth, String info)
               example     : new Passive: "Witches Curse" as witch passive, weakening consecutive melee hits by 0.5, up to 3 stacks per player, each decays after 3 seconds, applied to the player(generally less melee damage dealed to any mob for a short time)
               return      : void
  
             6.method      : preExecution is called by execution, to give the chance for a potential revive / afterlife passive
               declaration : public Double manipulateDamageDealing(SuperSmashKit to, String DamageType, Double amount, String info)
               example     : +1 for each ravage stack if damage type is "Melee"
               return      : expects 0.0 or less to be dead, otherwise the health amount to stay alive with, maybe a reviving passive or aftermath passive will be an idea(of your kit)
  
    3. effectsOverTime:
       -effectsOverTime are currently not completely implemented but provide a lot of potential of self and target affecting, ask me if you want something added

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
  //and allowing the initialization of the variables to be automatic from there, as well as giving the damageTypeCount and allowing
  //Strings to be used as DamageType in function calls instead of int, while the int = the index of the String in this array
  //Adding a DamageType here will allow you to immediatly apply damage of that type with an ability, but in order for Kits to have
  //resistance against it, you will need to add the new damage type resistance in the kit initialization that ends up calling SuperSmashKit.init(...Double[] resistances)
  private int taskID;
  protected Boolean SHOW_DEBUG_MESSAGES = false;
  protected Boolean DETAILED_DEBUG_MESSAGES = true;  //only matters while SHOW_DEBUG_MESSAGES == true  
  
  //not all DamageTypes need resistances, but the x resistances provided will be applied to the first x damage types
  protected static final String[] DamageTypes = { "True", "Melee", "Ability", "Projectile", "Explosion", "Fire", "Poison", "Wither" };
  private int damageTypeCount = getdamageTypeCount(); //is initialized automatically
  private Double maxHealth = 20.0;
  private Double healthRegenerationPerTick = 0.0;

  private Double attackDamage = 1.0;
  private Double knockbackDealed = 1.0; //with Attacks since Abilities have their own Kb values; 1 = 100% so default
  private Double knockbackTaken = 1.0;

  private Double[] resistances = {0.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0};

//Variables for private and public acess
  private Double health = 20.0;
  private Double hunger = 20.0;
  

  private IntegerBoolean allowRegeneration = new IntegerBoolean(0, true); //Can technically be a custom Effect
  private IntegerBoolean allowHealing = new IntegerBoolean(0, true);

  private List<EffectOverTime> effects = new ArrayList(0); //effectsOverTime have a bool "removeOnTouchingGround" a String ID and SuperSmashKit by for a class to check if it applied it

  private IntegerBoolean invulnerable = new IntegerBoolean(0,false); //Will be caused to set true by applyGeneralInvulnerability(int Duration)
  private IntegerBoolean[] invulnerabilities = new IntegerBoolean[getdamageTypeCount()]; //Custom Damage types, length damageTypeCount(DTC)

  private List<IntegerDouble> maxHealthIncreasesPreMult = new ArrayList(0);
  private List<IntegerDouble> maxHealthReductionsPreMult = new ArrayList(0);
  private List<IntegerDouble> maxHealthMultipliers = new ArrayList(0);
  private List<IntegerDouble> maxHealthIncreasesPostMult = new ArrayList(0);
  private List<IntegerDouble> maxHealthReductionsPostMult = new ArrayList(0);

  private List<IntegerDouble> healthRegenerationIncreasesPreMult = new ArrayList(0);
  private List<IntegerDouble> healthRegenerationReductionsPreMult = new ArrayList(0);
  private List<IntegerDouble> healthRegenerationMultipliers = new ArrayList(0);
  private List<IntegerDouble> healthRegenerationIncreasesPostMult = new ArrayList(0);
  private List<IntegerDouble> healthRegenerationReductionsPostMult = new ArrayList(0);


  private List<IntegerDouble>[] resistanceIncreasesPreMult   = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] resistanceReductionsPreMult  = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] resistanceMultipliers        = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] resistanceIncreasesPostMult  = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] resistanceReductionsPostMult = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]


  private List<IntegerDouble>[] takenDamageIncreasesPreMult   = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] takenDamageReductionsPreMult  = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] takenDamageMultipliers        = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] takenDamageIncreasesPostMult  = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] takenDamageReductionsPostMult = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]

  private List<IntegerDouble>[] inflictedDamageIncreasesPreMult   = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] inflictedDamageReductionsPreMult  = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] inflictedDamageMultipliers        = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] inflictedDamageIncreasesPostMult  = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]
  private List<IntegerDouble>[] inflictedDamageReductionsPostMult = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Can be multiple ones at once List<>; Is for each Damage Type[]

  private List<IntegerDouble>[] knockbackTakenMultipliers            = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Since TRUE Damage wont deal knockback, DamageType 0/True will be for general modifiers
  private List<IntegerVector>[] knockbackTakenDirectionalMultipliers = (ArrayList<IntegerVector>[])new ArrayList[damageTypeCount]; 

  private List<IntegerDouble>[] knockbackDealedMultipliers            = (ArrayList<IntegerDouble>[])new ArrayList[damageTypeCount]; //Since TRUE Damage wont deal knockback, DamageType 0/True will be for general modifiers
  private List<IntegerVector>[] knockbackDealedDirectionalMultipliers = (ArrayList<IntegerVector>[])new ArrayList[damageTypeCount]; 

  
/////Variables that allow/deny functions that cause over time effects/modifiers; All init true
  
  protected IntegerBoolean allow_applymaxHealthReduction  = new IntegerBoolean(0, true);  //The Integer is the Timer until false becomes true again
  protected IntegerBoolean allow_clearmaxHealthReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applymaxHealthIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearmaxHealthIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applymaxHealthMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearmaxHealthMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyhealthRegenerationReduction = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearhealthRegenerationReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyhealthRegenerationIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearhealthRegenerationIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyhealthRegenerationMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearhealthRegenerationMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyGeneralInvulnerability = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearGeneralInvulnerability = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyInvulnerability = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearinvulnerabilities = new IntegerBoolean(0, true); 

  
  protected IntegerBoolean allow_applyResistanceReduction = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearResistanceReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyResistanceIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearResistanceIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyResistanceMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearresistanceMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyTakenDamageReduction = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearTakenDamageReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyTakenDamageIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearTakenDamageIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyTakenDamageMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_cleartakenDamageMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyDealedDamageReduction = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearDealedDamageReductions = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyDealedDamageIncrease = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearDealedDamageIncreases = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyDealedDamageMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearDealedDamageMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyknockbackTakenMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearknockbackTakenMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyknockbackTakenDirectionalMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearknockbackTakenDirectionalMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyknockbackDealedMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearknockbackDealedMultipliers = new IntegerBoolean(0, true);

  protected IntegerBoolean allow_applyknockbackDealedDirectionalMultiplier = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_clearknockbackDealedDirectionalMultipliers = new IntegerBoolean(0, true);

  
  protected IntegerBoolean allow_EffectRemove = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_EffectExists = new IntegerBoolean(0, true);
  protected IntegerBoolean allow_EffectApply = new IntegerBoolean(0, true);


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//VisualSupportWhileScrollingFast/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  //FUNCTIONS To Override if needed:
  //manipulateDamageTaking is called before calling reducehealth
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
  public void afterLosinghealth(SuperSmashKit by, String DamageType, Double losthealth, String info) {
  }
  //return 0.0 or less to be dead, maybe a reviving passive or aftermath passive will be an idea
  public Double preExecution(SuperSmashKit by, String DamageType, Double fatalDamageAmount, String info) {
    return 0.0;
  }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//VisualSupportWhileScrollingFast/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  //FUNCTIONS
  
  protected int getdamageTypeCount(){
    return DamageTypes.length;  
    }

  //Convert a String DamageType into the connected int; returns -1 if invalid
  protected int stringDamageTypeToInt(String DamageType) {
    for ( int i = 0; i < getdamageTypeCount() ; i++ ) {
      if (DamageTypes[i] == DamageType) {
        return i;  
      } 
    }
    return -1;
  }
  
  protected String intDamageTypeToString(int DamageType) {
    if ((DamageTypes.length <= DamageType)||(DamageType<0)) {
      return "None";
    }
    else {
      return DamageTypes[DamageType];
    }
  }
  
  protected Double gethealth() {
    return health;
  }
  
  protected Double gethealthPercentage() {
    return (100/maxHealth*health);
  }

  
  private List<IntegerDouble>[] initHelp(List<IntegerDouble>[] toChange) {
    for ( int i = 0 ; i < damageTypeCount ; i++ ) {
      toChange[i] = new ArrayList(0);
    }
    return toChange;
  }
 
 private List<IntegerVector>[] initHelp2(List<IntegerVector>[] toChange) {
    for ( int i = 0 ; i < damageTypeCount ; i++ ) {
      toChange[i] = new ArrayList(0);
    }
    return toChange;
  }
  
///////////////////  
  protected void init(Double initmaxHealth, Double inithealthRegenerationPerSecond, Double initattackDamage, Double initknockbackDealed, Double initknockbackTaken, Double[] initresistances){
    //initialize the permanent values
    Double inithealthRegenerationPerTick = (inithealthRegenerationPerSecond / 20);
    if (initmaxHealth > 0) {
      maxHealth = initmaxHealth;
    }
    healthRegenerationPerTick = inithealthRegenerationPerTick;
    attackDamage = initattackDamage;
    knockbackDealed = initknockbackDealed;
    knockbackTaken = initknockbackTaken;
    
    resistances = new Double[damageTypeCount];
    
    for (int i = 0; i < initresistances.length; i++ ) {
      resistances[i] = initresistances[i];
    }
      
    //initialize the rest of the kit  
    health = maxHealth;
  
    for ( int i = 0 ; i < damageTypeCount ; i++ ) {
        invulnerabilities[i] = new IntegerBoolean(0,false);
    }
  
    resistanceIncreasesPreMult = initHelp(resistanceIncreasesPreMult);
    resistanceReductionsPreMult = initHelp(resistanceReductionsPreMult);
    resistanceMultipliers = initHelp(resistanceMultipliers);
    resistanceIncreasesPostMult = initHelp(resistanceIncreasesPostMult);
    resistanceReductionsPostMult = initHelp(resistanceReductionsPostMult);

    takenDamageIncreasesPreMult = initHelp(takenDamageIncreasesPreMult);
    takenDamageReductionsPreMult = initHelp(takenDamageReductionsPreMult);
    takenDamageMultipliers = initHelp(takenDamageMultipliers);
    takenDamageIncreasesPostMult = initHelp(takenDamageIncreasesPostMult);
    takenDamageReductionsPostMult = initHelp(takenDamageReductionsPostMult);

    inflictedDamageIncreasesPreMult = initHelp(inflictedDamageIncreasesPreMult);
    inflictedDamageReductionsPreMult = initHelp(inflictedDamageReductionsPreMult);
    inflictedDamageMultipliers = initHelp(inflictedDamageMultipliers);
    inflictedDamageIncreasesPostMult = initHelp(inflictedDamageIncreasesPostMult);
    inflictedDamageReductionsPostMult = initHelp(inflictedDamageReductionsPostMult);

    knockbackTakenMultipliers = initHelp(knockbackTakenMultipliers);
    knockbackTakenDirectionalMultipliers = initHelp2(knockbackTakenDirectionalMultipliers);
    
    knockbackDealedMultipliers = initHelp(knockbackDealedMultipliers);
    knockbackDealedDirectionalMultipliers = initHelp2(knockbackDealedDirectionalMultipliers);

    
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
    Double ToReturn = resistances[DamageType];
    //apply all modifiers
    
    for ( int i = 0 ; i < resistanceIncreasesPreMult[DamageType].size() ; i++ ) {
      ToReturn = ToReturn + resistanceIncreasesPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < resistanceReductionsPreMult[DamageType].size() ; i++ ) {
      ToReturn = ToReturn - resistanceReductionsPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < resistanceMultipliers[DamageType].size() ; i++ ) {
      ToReturn = ToReturn * resistanceMultipliers[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < resistanceIncreasesPostMult[DamageType].size() ; i++ ) {
      ToReturn = ToReturn + resistanceIncreasesPostMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < resistanceReductionsPostMult[DamageType].size() ; i++ ) {
      ToReturn = ToReturn - resistanceReductionsPostMult[DamageType].get(i).DoubleValue;
    }

    return ToReturn;
  }
  
  private Double calculateDamageWithresistances(int DamageType, Double amount) {
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
    
    for ( int i = 0 ; i < takenDamageIncreasesPreMult[DamageType].size() ; i++ ) {
      amount = amount + takenDamageIncreasesPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < takenDamageReductionsPreMult[DamageType].size() ; i++ ) {
      amount = amount - takenDamageReductionsPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < takenDamageMultipliers[DamageType].size() ; i++ ) {
      amount = amount * takenDamageMultipliers[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < takenDamageIncreasesPostMult[DamageType].size() ; i++ ) {
      amount = amount + takenDamageIncreasesPostMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < takenDamageReductionsPostMult[DamageType].size() ; i++ ) {
      amount = amount - takenDamageReductionsPostMult[DamageType].get(i).DoubleValue;
    }

    return amount;
  }

  private Double calculateDamageToBeInflicted(int DamageType, Double amount) {
    //apply all modifiers
    
    for ( int i = 0 ; i < inflictedDamageIncreasesPreMult[DamageType].size() ; i++ ) {
      amount = amount + inflictedDamageIncreasesPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < inflictedDamageReductionsPreMult[DamageType].size() ; i++ ) {
      amount = amount - inflictedDamageReductionsPreMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < inflictedDamageMultipliers[DamageType].size() ; i++ ) {
      amount = amount * inflictedDamageMultipliers[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < inflictedDamageIncreasesPostMult[DamageType].size() ; i++ ) {
      amount = amount + inflictedDamageIncreasesPostMult[DamageType].get(i).DoubleValue;
    }
    for ( int i = 0 ; i < inflictedDamageReductionsPostMult[DamageType].size() ; i++ ) {
      amount = amount - inflictedDamageReductionsPostMult[DamageType].get(i).DoubleValue;
    }

    return amount;
  }
  
  //Is called by reducehealth on fatal damage
  private void execution(SuperSmashKit by, int DamageType, Double amount, String info) {
    Double isStillAlive = preExecution(by, intDamageTypeToString(DamageType), amount, info);
    if (isStillAlive <= 0.0) {
      //Actual execution, stats, cause dekitting, . . .
      //TODO: look above
    }
    else {
      //Death was somehow prevented
      health = isStillAlive;
    }
  }
  
  //Reduces health and triggers the Execution function on death,
  //will return the health that was left on death instead of the damage that was passed
  private Double reducehealth(SuperSmashKit by, int DamageType, Double amount, String info) {
    Double ToReturn = 0.0; //Init the value that will be returned
    
    //if the damage is not lethal
    if (health >= amount) {
      health = health - amount;  //reduce the health
      ToReturn = amount;         //return the amount of damage dealed
    }
    else {  //if the damage is lethal
      ToReturn = health; //return the amount of health left as damage dealed
      health = 0.0;      //reduce the health
      execution(by, DamageType, ToReturn, info);
    }
    
    refresh();  
    return ToReturn; 
  }
    
  private Double Increasehealth(Double amount) {
    Double ToReturn = 0.0; //Init the value that will be returned
    
    //if the healing will not overheal
    if (health + amount <= maxHealth) {
      health = health + amount;  //increase the health
      ToReturn = amount;         //return the amount of health healed
    }
    else {  //if the healing would overheal
      ToReturn = maxHealth - health; //return the amount of health healed
      health = maxHealth;      //increase the health
    }
     
    refresh();
    return ToReturn; 
  }
  
///////////////////////// 
  protected void inflictSelfDamage(Double amount) {
    reducehealth(this, stringDamageTypeToInt("True"), amount, "");
  }

  private Double takeDamage(SuperSmashKit by, int DamageType, Double amount, String info) { //Info can be empty or can contain additions, for example an ability name
    Double dmgToBeTaken = 0.0;
    Double dmgTaken = 0.0;
    
    //Nothing happens if amount <= 0
    if (amount > 0.0) {
      //Check if invulnerable or invulnerable to that damage type
      if ((invulnerable.BooleanValue)||(invulnerabilities[DamageType].BooleanValue)) {
        //Nothing happens, ToReturn is already 0 
      }
      else {
        //calculate the damage, reduce the health and return the actually dealed damage
        dmgToBeTaken = calculateDamageWithresistances(DamageType, calculateDamageToBeTaken(DamageType, amount));
        dmgToBeTaken = manipulateDamageTaking(by, intDamageTypeToString(DamageType), dmgToBeTaken, info);
        dmgTaken = reducehealth(by, DamageType, dmgToBeTaken, info);
        afterLosinghealth(by, intDamageTypeToString(DamageType), dmgTaken, info);
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
      amount = attackDamage;
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
    for ( int i = 0 ; i<knockbackTakenMultipliers[DamageType].size() ; i++) {
      ToReturn.multiply(knockbackTakenMultipliers[DamageType].get(i).DoubleValue);
    }
    for ( int i = 0 ; i<knockbackTakenDirectionalMultipliers[DamageType].size() ; i++) {
      ToReturn.multiply(knockbackTakenDirectionalMultipliers[DamageType].get(i).VectorValue);
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
    for ( int i = 0 ; i<knockbackDealedMultipliers[DamageType].size() ; i++) {
      ToReturn.multiply(knockbackDealedMultipliers[DamageType].get(i).DoubleValue);
    }
    for ( int i = 0 ; i<knockbackDealedDirectionalMultipliers[DamageType].size() ; i++) {
      ToReturn.multiply(knockbackDealedDirectionalMultipliers[DamageType].get(i).VectorValue);
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
  protected void applySelfVelocity(Vector v) { //Info can be empty or can contain additions, for example an ability name
    this.takeKnockback(this, 0, v, "");
  }

  
/////////////////////////  
  protected Double heal(Double amount) {
    Double ToReturn = 0.0;
    //if healing is not disabled
    if (allowHealing.BooleanValue) {
      ToReturn = Increasehealth(amount);
    }   
      
    return ToReturn;
  }

  private void onEffectOverTimeExpire() {   //called by processeffectsOverTime;  for example a custom damage over time effect that denys regeneration
    //No effects that do something on Expiring yet
  }
  private void processeffectsOverTime() { //called by onTick

    for ( int i = 0 ; i < effects.size() ; i++) {
      if (effects.get(i).timer > 0) {
        effects.get(i).timer = effects.get(i).timer - 1;
      }
    }
    
    //No effectsOverTime declared yet
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
    
    //getPlayer().sendMessage("invulnerable");
    if  (invulnerable.BooleanValue) {  //If invulnerable = true
      if (invulnerable.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        invulnerable.IntegerValue = invulnerable.IntegerValue -1;
      }     
      if (invulnerable.IntegerValue == 0) {//if the timer expired
        invulnerable.BooleanValue = false;  //set invulnerable to false
      }
    }

    //getPlayer().sendMessage("invulnerabilities");
    for ( int i = 0 ; i < damageTypeCount ; i++) {
      if (invulnerabilities[i].BooleanValue) {
        if (invulnerabilities[i].IntegerValue > 0) {
          invulnerabilities[i].IntegerValue = invulnerabilities[i].IntegerValue - 1;
        }
        if (invulnerabilities[i].IntegerValue == 0) {
          invulnerabilities[i].BooleanValue = false;
        }
      }
    }
    
    
    //getPlayer().sendMessage("processModifiersHelp");
    maxHealthIncreasesPreMult = processModifiersHelp(maxHealthIncreasesPreMult);
    maxHealthReductionsPreMult = processModifiersHelp(maxHealthReductionsPreMult);
    maxHealthMultipliers = processModifiersHelp(maxHealthMultipliers);
    maxHealthIncreasesPostMult = processModifiersHelp(maxHealthIncreasesPostMult);
    maxHealthReductionsPostMult = processModifiersHelp(maxHealthReductionsPostMult);

    healthRegenerationIncreasesPreMult = processModifiersHelp(healthRegenerationIncreasesPreMult);
    healthRegenerationReductionsPreMult = processModifiersHelp(healthRegenerationReductionsPreMult);
    healthRegenerationMultipliers = processModifiersHelp(healthRegenerationMultipliers);
    healthRegenerationIncreasesPostMult = processModifiersHelp(healthRegenerationIncreasesPostMult);
    healthRegenerationReductionsPostMult = processModifiersHelp(healthRegenerationReductionsPostMult);

    for ( int i = 0 ; i<resistanceIncreasesPreMult.length ; i++ ) {
      resistanceIncreasesPreMult[i] = processModifiersHelp(resistanceIncreasesPreMult[i]);
    }
    for ( int i = 0 ; i<resistanceReductionsPreMult.length ; i++ ) {
      resistanceReductionsPreMult[i] = processModifiersHelp(resistanceReductionsPreMult[i]);
    }
    for ( int i = 0 ; i<resistanceMultipliers.length ; i++ ) {
      resistanceMultipliers[i] = processModifiersHelp(resistanceMultipliers[i]);
    }
    for ( int i = 0 ; i<resistanceIncreasesPostMult.length ; i++ ) {
      resistanceIncreasesPostMult[i] = processModifiersHelp(resistanceIncreasesPostMult[i]);
    }
    for ( int i = 0 ; i<resistanceReductionsPostMult.length ; i++ ) {
      resistanceReductionsPostMult[i] = processModifiersHelp(resistanceReductionsPostMult[i]);
    }

    for ( int i = 0 ; i<takenDamageIncreasesPreMult.length ; i++ ) {
      takenDamageIncreasesPreMult[i] = processModifiersHelp(takenDamageIncreasesPreMult[i]);
    }
    for ( int i = 0 ; i<takenDamageReductionsPreMult.length ; i++ ) {
      takenDamageReductionsPreMult[i] = processModifiersHelp(takenDamageReductionsPreMult[i]);
    }
    for ( int i = 0 ; i<takenDamageMultipliers.length ; i++ ) {
      takenDamageMultipliers[i] = processModifiersHelp(takenDamageMultipliers[i]);
    }
    for ( int i = 0 ; i<takenDamageIncreasesPostMult.length ; i++ ) {
      takenDamageIncreasesPostMult[i] = processModifiersHelp(takenDamageIncreasesPostMult[i]);
    }
    for ( int i = 0 ; i<takenDamageReductionsPostMult.length ; i++ ) {
      takenDamageReductionsPostMult[i] = processModifiersHelp(takenDamageReductionsPostMult[i]);
    }

    for ( int i = 0 ; i<inflictedDamageIncreasesPreMult.length ; i++ ) {
      inflictedDamageIncreasesPreMult[i] = processModifiersHelp(inflictedDamageIncreasesPreMult[i]);
    }
    for ( int i = 0 ; i<inflictedDamageReductionsPreMult.length ; i++ ) {
      inflictedDamageReductionsPreMult[i] = processModifiersHelp(inflictedDamageReductionsPreMult[i]);
    }
    for ( int i = 0 ; i<inflictedDamageMultipliers.length ; i++ ) {
      inflictedDamageMultipliers[i] = processModifiersHelp(inflictedDamageMultipliers[i]);
    }
    for ( int i = 0 ; i<inflictedDamageIncreasesPostMult.length ; i++ ) {
      inflictedDamageIncreasesPostMult[i] = processModifiersHelp(inflictedDamageIncreasesPostMult[i]);
    }
    for ( int i = 0 ; i<inflictedDamageReductionsPostMult.length ; i++ ) {
      inflictedDamageReductionsPostMult[i] = processModifiersHelp(inflictedDamageReductionsPostMult[i]);
    }

    for ( int i = 0 ; i<knockbackTakenMultipliers.length ; i++ ) {
      knockbackTakenMultipliers[i] = processModifiersHelp(knockbackTakenMultipliers[i]);
    }
    for ( int i = 0 ; i<knockbackTakenDirectionalMultipliers.length ; i++ ) {
      knockbackTakenDirectionalMultipliers[i] = processModifiersHelp2(knockbackTakenDirectionalMultipliers[i]);
    }

    for ( int i = 0 ; i<knockbackDealedMultipliers.length ; i++ ) {
      knockbackDealedMultipliers[i] = processModifiersHelp(knockbackDealedMultipliers[i]);
    }
    for ( int i = 0 ; i<knockbackDealedDirectionalMultipliers.length ; i++ ) {
      knockbackDealedDirectionalMultipliers[i] = processModifiersHelp2(knockbackDealedDirectionalMultipliers[i]);
    }
    
  }
  
  private void processFunctionBlockades() { //If allow_... vars are set to false and the tick count is not negative, process their ticks!
  
    if  (! allow_applymaxHealthReduction.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_applymaxHealthReduction.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_applymaxHealthReduction.IntegerValue = allow_applymaxHealthReduction.IntegerValue -1;
      }     
      if (allow_applymaxHealthReduction.IntegerValue == 0) {//if the timer expired
        allow_applymaxHealthReduction.BooleanValue = true;  //allow the function again
      }
    }
    if  (! allow_clearmaxHealthReductions.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_clearmaxHealthReductions.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_clearmaxHealthReductions.IntegerValue = allow_clearmaxHealthReductions.IntegerValue -1;
      }     
      if (allow_clearmaxHealthReductions.IntegerValue == 0) {//if the timer expired
        allow_clearmaxHealthReductions.BooleanValue = true;  //allow the function again
      }
    }
 
    if  (! allow_applymaxHealthIncrease.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_applymaxHealthIncrease.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_applymaxHealthIncrease.IntegerValue = allow_applymaxHealthIncrease.IntegerValue -1;
      }     
      if (allow_applymaxHealthIncrease.IntegerValue == 0) {//if the timer expired
        allow_applymaxHealthIncrease.BooleanValue = true;  //allow the function again
      }
    }
    if  (! allow_clearmaxHealthIncreases.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_clearmaxHealthIncreases.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_clearmaxHealthIncreases.IntegerValue = allow_clearmaxHealthIncreases.IntegerValue -1;
      }     
      if (allow_clearmaxHealthIncreases.IntegerValue      == 0) {//if the timer expired
        allow_clearmaxHealthIncreases.BooleanValue = true;  //allow the function again
      }
    }
  
    if  (! allow_applymaxHealthMultiplier.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_applymaxHealthMultiplier.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_applymaxHealthMultiplier.IntegerValue = allow_applymaxHealthMultiplier.IntegerValue -1;
      }     
      if (allow_applymaxHealthMultiplier.IntegerValue == 0) {//if the timer expired
        allow_applymaxHealthMultiplier.BooleanValue = true;  //allow the function again
      }
    }
    if  (! allow_clearmaxHealthMultipliers.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_clearmaxHealthMultipliers.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_clearmaxHealthMultipliers.IntegerValue = allow_clearmaxHealthMultipliers.IntegerValue -1;
      }     
      if (allow_clearmaxHealthMultipliers.IntegerValue == 0) {//if the timer expired
        allow_clearmaxHealthMultipliers.BooleanValue = true;  //allow the function again
      }
    }
  
    if  (! allow_applyhealthRegenerationReduction.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_applyhealthRegenerationReduction.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_applyhealthRegenerationReduction.IntegerValue = allow_applyhealthRegenerationReduction.IntegerValue -1;
      }     
      if (allow_applyhealthRegenerationReduction.IntegerValue == 0) {//if the timer expired
        allow_applyhealthRegenerationReduction.BooleanValue = true;  //allow the function again
      }
    }
    if  (! allow_clearhealthRegenerationReductions.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_clearhealthRegenerationReductions.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_clearhealthRegenerationReductions.IntegerValue = allow_clearhealthRegenerationReductions.IntegerValue -1;
      }     
      if (allow_clearhealthRegenerationReductions.IntegerValue == 0) {//if the timer expired
        allow_clearhealthRegenerationReductions.BooleanValue = true;  //allow the function again
      }
    }

    if  (! allow_applyhealthRegenerationIncrease.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_applyhealthRegenerationIncrease.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_applyhealthRegenerationIncrease.IntegerValue = allow_applyhealthRegenerationIncrease.IntegerValue -1;
      }     
      if (allow_applyhealthRegenerationIncrease.IntegerValue == 0) {//if the timer expired
        allow_applyhealthRegenerationIncrease.BooleanValue = true;  //allow the function again
      }
    }
    if  (! allow_clearhealthRegenerationIncreases.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_clearhealthRegenerationIncreases.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_clearhealthRegenerationIncreases.IntegerValue = allow_clearhealthRegenerationIncreases.IntegerValue -1;
      }     
      if (allow_clearhealthRegenerationIncreases.IntegerValue == 0) {//if the timer expired
        allow_clearhealthRegenerationIncreases.BooleanValue = true;  //allow the function again
      }
    }
    
    if  (! allow_applyhealthRegenerationReduction.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_applyhealthRegenerationReduction.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_applyhealthRegenerationReduction.IntegerValue = allow_applyhealthRegenerationReduction.IntegerValue -1;
      }     
      if (allow_applyhealthRegenerationReduction.IntegerValue == 0) {//if the timer expired
        allow_applyhealthRegenerationReduction.BooleanValue = true;  //allow the function again
      }
    }
    if  (! allow_clearhealthRegenerationReductions.BooleanValue) {  //The Integer is the Timer until false becomes true again
      if (allow_clearhealthRegenerationReductions.IntegerValue > 0) { //if the Tick count is > 0; reduce it by 1
        allow_clearhealthRegenerationReductions.IntegerValue = allow_clearhealthRegenerationReductions.IntegerValue     -1;
      }     
      if (allow_clearhealthRegenerationReductions.IntegerValue == 0) {//if the timer expired
        allow_clearhealthRegenerationReductions.BooleanValue = true;  //allow the function again
      }
    }

    if  (! allow_applyhealthRegenerationMultiplier.BooleanValue) {
      if (allow_applyhealthRegenerationMultiplier.IntegerValue > 0) {
        allow_applyhealthRegenerationMultiplier.IntegerValue = allow_applyhealthRegenerationMultiplier.IntegerValue -1;
      }
      if (allow_applyhealthRegenerationMultiplier.IntegerValue == 0) {
        allow_applyhealthRegenerationMultiplier.BooleanValue = true;
      }
    }
    if  (! allow_clearhealthRegenerationMultipliers.BooleanValue) {
      if (allow_clearhealthRegenerationMultipliers.IntegerValue > 0) {
        allow_clearhealthRegenerationMultipliers.IntegerValue = allow_clearhealthRegenerationMultipliers.IntegerValue -1;
      }
      if (allow_clearhealthRegenerationMultipliers.IntegerValue == 0) {
        allow_clearhealthRegenerationMultipliers.BooleanValue = true;
      }
    }

    if  (! allow_applyGeneralInvulnerability.BooleanValue) {
      if (allow_applyGeneralInvulnerability.IntegerValue > 0) {
        allow_applyGeneralInvulnerability.IntegerValue = allow_applyGeneralInvulnerability.IntegerValue -1;
      }
      if (allow_applyGeneralInvulnerability.IntegerValue == 0) {
        allow_applyGeneralInvulnerability.BooleanValue = true;
      }
    }
    if  (! allow_clearGeneralInvulnerability.BooleanValue) {
      if (allow_clearGeneralInvulnerability.IntegerValue > 0) {
        allow_clearGeneralInvulnerability.IntegerValue = allow_clearGeneralInvulnerability.IntegerValue -1;
      }
      if (allow_clearGeneralInvulnerability.IntegerValue == 0) {
        allow_clearGeneralInvulnerability.BooleanValue = true;
      }
    }

    if  (! allow_applyInvulnerability.BooleanValue) {
      if (allow_applyInvulnerability.IntegerValue > 0) {
        allow_applyInvulnerability.IntegerValue = allow_applyInvulnerability.IntegerValue -1;
      }
      if (allow_applyInvulnerability.IntegerValue == 0) {
        allow_applyInvulnerability.BooleanValue = true;
      }
    }
    if  (! allow_clearinvulnerabilities.BooleanValue) {
      if (allow_clearinvulnerabilities.IntegerValue > 0) {
        allow_clearinvulnerabilities.IntegerValue = allow_clearinvulnerabilities.IntegerValue -1;
      }
      if (allow_clearinvulnerabilities.IntegerValue == 0) {
        allow_clearinvulnerabilities.BooleanValue = true;
      }
    }

  
    if  (! allow_applyResistanceReduction.BooleanValue) {
      if (allow_applyResistanceReduction.IntegerValue > 0) {
        allow_applyResistanceReduction.IntegerValue = allow_applyResistanceReduction.IntegerValue -1;
      }
      if (allow_applyResistanceReduction.IntegerValue == 0) {
        allow_applyResistanceReduction.BooleanValue = true;
      }
    }
    if  (! allow_clearResistanceReductions.BooleanValue) {
      if (allow_clearResistanceReductions.IntegerValue > 0) {
        allow_clearResistanceReductions.IntegerValue = allow_clearResistanceReductions.IntegerValue -1;
      }
      if (allow_clearResistanceReductions.IntegerValue == 0) {
        allow_clearResistanceReductions.BooleanValue = true;
      }
    }

    if  (! allow_applyResistanceIncrease.BooleanValue) {
      if (allow_applyResistanceIncrease.IntegerValue > 0) {
        allow_applyResistanceIncrease.IntegerValue = allow_applyResistanceIncrease.IntegerValue -1;
      }
      if (allow_applyResistanceIncrease.IntegerValue == 0) {
        allow_applyResistanceIncrease.BooleanValue = true;
      }
    }
    if  (! allow_clearResistanceIncreases.BooleanValue) {
      if (allow_clearResistanceIncreases.IntegerValue > 0) {
        allow_clearResistanceIncreases.IntegerValue = allow_clearResistanceIncreases.IntegerValue -1;
      }
      if (allow_clearResistanceIncreases.IntegerValue == 0) {
        allow_clearResistanceIncreases.BooleanValue = true;
      }
    }

    if  (! allow_applyResistanceMultiplier.BooleanValue) {
      if (allow_applyResistanceMultiplier.IntegerValue > 0) {
        allow_applyResistanceMultiplier.IntegerValue = allow_applyResistanceMultiplier.IntegerValue -1;
      }
      if (allow_applyResistanceMultiplier.IntegerValue == 0) {
        allow_applyResistanceMultiplier.BooleanValue = true;
      }
    }
    if  (! allow_clearresistanceMultipliers.BooleanValue) {
      if (allow_clearresistanceMultipliers.IntegerValue > 0) {
        allow_clearresistanceMultipliers.IntegerValue = allow_clearresistanceMultipliers.IntegerValue -1;
      }
      if (allow_clearresistanceMultipliers.IntegerValue == 0) {
        allow_clearresistanceMultipliers.BooleanValue = true;
      }
    }

    if  (! allow_applyTakenDamageReduction.BooleanValue) {
      if (allow_applyTakenDamageReduction.IntegerValue > 0) {
        allow_applyTakenDamageReduction.IntegerValue = allow_applyTakenDamageReduction.IntegerValue -1;
      }
      if (allow_applyTakenDamageReduction.IntegerValue == 0) {
        allow_applyTakenDamageReduction.BooleanValue = true;
      }
    }
    if  (! allow_clearTakenDamageReductions.BooleanValue) {
      if (allow_clearTakenDamageReductions.IntegerValue > 0) {
        allow_clearTakenDamageReductions.IntegerValue = allow_clearTakenDamageReductions.IntegerValue -1;
      }
      if (allow_clearTakenDamageReductions.IntegerValue == 0) {
        allow_clearTakenDamageReductions.BooleanValue = true;
      }
    }

    if  (! allow_applyTakenDamageIncrease.BooleanValue) {
      if (allow_applyTakenDamageIncrease.IntegerValue > 0) {
        allow_applyTakenDamageIncrease.IntegerValue = allow_applyTakenDamageIncrease.IntegerValue -1;
      }
      if (allow_applyTakenDamageIncrease.IntegerValue == 0) {
        allow_applyTakenDamageIncrease.BooleanValue = true;
      }
    }
    if  (! allow_clearTakenDamageIncreases.BooleanValue) {
      if (allow_clearTakenDamageIncreases.IntegerValue > 0) {
        allow_clearTakenDamageIncreases.IntegerValue = allow_clearTakenDamageIncreases.IntegerValue -1;
      }
      if (allow_clearTakenDamageIncreases.IntegerValue == 0) {
        allow_clearTakenDamageIncreases.BooleanValue = true;
      }
    }

    if  (! allow_applyTakenDamageMultiplier.BooleanValue) {
      if (allow_applyTakenDamageMultiplier.IntegerValue > 0) {
        allow_applyTakenDamageMultiplier.IntegerValue = allow_applyTakenDamageMultiplier.IntegerValue -1;
      }
      if (allow_applyTakenDamageMultiplier.IntegerValue == 0) {
        allow_applyTakenDamageMultiplier.BooleanValue = true;
      }
    }
    if  (! allow_cleartakenDamageMultipliers.BooleanValue) {
      if (allow_cleartakenDamageMultipliers.IntegerValue > 0) {
        allow_cleartakenDamageMultipliers.IntegerValue = allow_cleartakenDamageMultipliers.IntegerValue -1;
      }
      if (allow_cleartakenDamageMultipliers.IntegerValue == 0) {
        allow_cleartakenDamageMultipliers.BooleanValue = true;
      }
    }

    if  (! allow_applyDealedDamageReduction.BooleanValue) {
      if (allow_applyDealedDamageReduction.IntegerValue > 0) {
        allow_applyDealedDamageReduction.IntegerValue = allow_applyDealedDamageReduction.IntegerValue -1;
      }
      if (allow_applyDealedDamageReduction.IntegerValue == 0) {
        allow_applyDealedDamageReduction.BooleanValue = true;
      }
    }
    if  (! allow_clearDealedDamageReductions.BooleanValue) {
      if (allow_clearDealedDamageReductions.IntegerValue > 0) {
        allow_clearDealedDamageReductions.IntegerValue = allow_clearDealedDamageReductions.IntegerValue -1;
      }
      if (allow_clearDealedDamageReductions.IntegerValue      == 0) {
        allow_clearDealedDamageReductions.BooleanValue = true;
      }
    }

    if  (! allow_applyDealedDamageIncrease.BooleanValue) {
      if (allow_applyDealedDamageIncrease.IntegerValue > 0) {
        allow_applyDealedDamageIncrease.IntegerValue = allow_applyDealedDamageIncrease.IntegerValue -1;
      }
      if (allow_applyDealedDamageIncrease.IntegerValue == 0) {
        allow_applyDealedDamageIncrease.BooleanValue = true;
      }
    }
    if  (! allow_clearDealedDamageIncreases.BooleanValue) {
      if (allow_clearDealedDamageIncreases.IntegerValue > 0) {
        allow_clearDealedDamageIncreases.IntegerValue = allow_clearDealedDamageIncreases.IntegerValue -1;
      }
      if (allow_clearDealedDamageIncreases.IntegerValue == 0) {
        allow_clearDealedDamageIncreases.BooleanValue = true;
      }
    }

    if  (! allow_applyDealedDamageMultiplier.BooleanValue) {
      if (allow_applyDealedDamageMultiplier.IntegerValue > 0) {
        allow_applyDealedDamageMultiplier.IntegerValue = allow_applyDealedDamageMultiplier.IntegerValue -1;
      }
      if (allow_applyDealedDamageMultiplier.IntegerValue == 0) {
        allow_applyDealedDamageMultiplier.BooleanValue = true;
      }
    }
    if  (! allow_clearDealedDamageMultipliers.BooleanValue) {
      if (allow_clearDealedDamageMultipliers.IntegerValue > 0) {
        allow_clearDealedDamageMultipliers.IntegerValue = allow_clearDealedDamageMultipliers.IntegerValue -1;
      }
      if (allow_clearDealedDamageMultipliers.IntegerValue == 0) {
        allow_clearDealedDamageMultipliers.BooleanValue = true;
      }
    }

    if  (! allow_applyknockbackTakenMultiplier.BooleanValue) {
      if (allow_applyknockbackTakenMultiplier.IntegerValue > 0) {
        allow_applyknockbackTakenMultiplier.IntegerValue = allow_applyknockbackTakenMultiplier.IntegerValue -1;
      }
      if (allow_applyknockbackTakenMultiplier.IntegerValue == 0) {
        allow_applyknockbackTakenMultiplier.BooleanValue = true;
      }
    }
    if  (! allow_clearknockbackTakenMultipliers.BooleanValue) {
      if (allow_clearknockbackTakenMultipliers.IntegerValue > 0) {
        allow_clearknockbackTakenMultipliers.IntegerValue = allow_clearknockbackTakenMultipliers.IntegerValue -1;
      }
      if (allow_clearknockbackTakenMultipliers.IntegerValue == 0) {
        allow_clearknockbackTakenMultipliers.BooleanValue = true;
      }
    }

    if  (! allow_applyknockbackTakenDirectionalMultiplier.BooleanValue) {
      if (allow_applyknockbackTakenDirectionalMultiplier.IntegerValue > 0) {
        allow_applyknockbackTakenDirectionalMultiplier.IntegerValue = allow_applyknockbackTakenDirectionalMultiplier.IntegerValue -1;
      }
      if (allow_applyknockbackTakenDirectionalMultiplier.IntegerValue == 0) {
        allow_applyknockbackTakenDirectionalMultiplier.BooleanValue = true;
      }
    }
    if  (! allow_clearknockbackTakenDirectionalMultipliers.BooleanValue) {
      if (allow_clearknockbackTakenDirectionalMultipliers.IntegerValue > 0) {
        allow_clearknockbackTakenDirectionalMultipliers.IntegerValue = allow_clearknockbackTakenDirectionalMultipliers.IntegerValue -1;
      }
      if (allow_clearknockbackTakenDirectionalMultipliers.IntegerValue == 0) {
        allow_clearknockbackTakenDirectionalMultipliers.BooleanValue = true;
      }
    }

    if  (! allow_applyknockbackDealedDirectionalMultiplier.BooleanValue) {
      if (allow_applyknockbackDealedDirectionalMultiplier.IntegerValue > 0) {
        allow_applyknockbackDealedDirectionalMultiplier.IntegerValue = allow_applyknockbackDealedDirectionalMultiplier.IntegerValue -1;
      }
      if (allow_applyknockbackDealedDirectionalMultiplier.IntegerValue == 0) {
        allow_applyknockbackDealedDirectionalMultiplier.BooleanValue = true;
      }
    }
    if  (! allow_clearknockbackDealedMultipliers.BooleanValue) {
      if (allow_clearknockbackDealedMultipliers.IntegerValue > 0) {
        allow_clearknockbackDealedMultipliers.IntegerValue = allow_clearknockbackDealedMultipliers.IntegerValue -1;
      }
      if (allow_clearknockbackDealedMultipliers.IntegerValue == 0) {
        allow_clearknockbackDealedMultipliers.BooleanValue = true;
      }
    }

    if  (! allow_applyknockbackDealedDirectionalMultiplier.BooleanValue) {
      if (allow_applyknockbackDealedDirectionalMultiplier.IntegerValue > 0) {
        allow_applyknockbackDealedDirectionalMultiplier.IntegerValue = allow_applyknockbackDealedDirectionalMultiplier.IntegerValue -1;
      }
      if (allow_applyknockbackDealedDirectionalMultiplier.IntegerValue == 0) {
        allow_applyknockbackDealedDirectionalMultiplier.BooleanValue = true;
      }
    }
    if  (! allow_clearknockbackDealedDirectionalMultipliers.BooleanValue) {
      if (allow_clearknockbackDealedDirectionalMultipliers.IntegerValue > 0) {
        allow_clearknockbackDealedDirectionalMultipliers.IntegerValue = allow_clearknockbackDealedDirectionalMultipliers.IntegerValue -1;
      }
      if (allow_clearknockbackDealedDirectionalMultipliers.IntegerValue == 0) {
        allow_clearknockbackDealedDirectionalMultipliers.BooleanValue = true;
      }
    }
    
    
    if  (! allow_EffectRemove.BooleanValue) {
      if (allow_EffectRemove.IntegerValue > 0) {
        allow_EffectRemove.IntegerValue = allow_EffectRemove.IntegerValue -1;
      }
      if (allow_EffectRemove.IntegerValue == 0) {
        allow_EffectRemove.BooleanValue = true;
      }
    }
    if  (! allow_EffectExists.BooleanValue) {
      if (allow_EffectExists.IntegerValue > 0) {
        allow_EffectExists.IntegerValue = allow_EffectExists.IntegerValue -1;
      }
      if (allow_EffectExists.IntegerValue == 0) {
        allow_EffectExists.BooleanValue = true;
      }
    }
    if  (! allow_EffectApply.BooleanValue) {
      if (allow_EffectApply.IntegerValue > 0) {
        allow_EffectApply.IntegerValue = allow_EffectApply.IntegerValue -1;
      }
      if (allow_EffectApply.IntegerValue == 0) {
        allow_EffectApply.BooleanValue = true;
      }
    }

}
  
  private Double calculatehealthRegeneration() {
    Double ToReturn = healthRegenerationPerTick;

    for ( int i= 0 ;  i < healthRegenerationIncreasesPreMult.size() ; i++ ) {
      ToReturn = ToReturn + healthRegenerationIncreasesPreMult.get(i).DoubleValue;
    }
    for ( int i= 0 ;  i < healthRegenerationReductionsPreMult.size() ; i++ ) {
      ToReturn = ToReturn - healthRegenerationReductionsPreMult.get(i).DoubleValue;
    }
    for ( int i= 0 ;  i < healthRegenerationMultipliers.size() ; i++ ) {
      ToReturn = ToReturn * healthRegenerationMultipliers.get(i).DoubleValue;
    }
    for ( int i= 0 ;  i < healthRegenerationIncreasesPostMult.size() ; i++ ) {
      ToReturn = ToReturn + healthRegenerationIncreasesPostMult.get(i).DoubleValue;
    }
    for ( int i= 0 ;  i < healthRegenerationReductionsPostMult.size() ; i++ ) {
      ToReturn = ToReturn - healthRegenerationReductionsPostMult.get(i).DoubleValue;
    }

    return ToReturn;    
  }
  private void regenerate() {  //called by tick before refresh
    if (allowRegeneration.BooleanValue) {
      Increasehealth(calculatehealthRegeneration());
    }
  }
  private int calculatehealthToBeShown() {
    int ToReturn = (int)Math.round(20*(health/maxHealth));
    if (ToReturn == 0) {
      ToReturn = 1;
    }
    return ToReturn;
  }
  private void refreshhealth() { //called by refresh
  //TODO: refresh the Players health bar(using health and maxHealth)
    getPlayer().setHealth(calculatehealthToBeShown());
  }
  private void refreshhunger() { //called by refresh
  //Do nothing for now, until the hunger bar gets a usage
  }
  private void refresh() { //refreshes health Bar/hunger Bar/etc  is called in tick
    refreshhealth();
    refreshhunger();
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

  protected Boolean addmaxHealthReductionMult(SuperSmashKit by, Double amount, int DurationTicks, Boolean preMult) {
    if (allow_applymaxHealthReduction.BooleanValue) {
        if (preMult) {
          maxHealthReductionsPreMult.add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          maxHealthReductionsPostMult.add(new IntegerDouble(DurationTicks, amount));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearmaxHealthReductions(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (allow_clearmaxHealthReductions.BooleanValue) {
      if (preMult) {
        maxHealthReductionsPreMult.clear();
      }
      else {
        maxHealthReductionsPostMult.clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addmaxHealthIncrease(SuperSmashKit by, Double amount, int DurationTicks, Boolean preMult){
    if (allow_applymaxHealthIncrease.BooleanValue) {
        if (preMult) {
          maxHealthIncreasesPreMult.add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          maxHealthIncreasesPostMult.add(new IntegerDouble(DurationTicks, amount));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearmaxHealthIncreases(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
      if (allow_clearmaxHealthIncreases.BooleanValue) {
      if (preMult) {
        maxHealthIncreasesPreMult.clear();
      }
      else {
        maxHealthIncreasesPostMult.clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addmaxHealthMultiplier(SuperSmashKit by, Double amount, int DurationTicks) {
    if (allow_applymaxHealthMultiplier.BooleanValue) {
      maxHealthMultipliers.add(new IntegerDouble(DurationTicks, amount));
      return true;
    }
    else {
      return false; 
    }  
  }
  protected Boolean clearmaxHealthMultipliers() { //-1 Ticks shouldnt be cleared but currently are
    if (allow_clearmaxHealthMultipliers.BooleanValue) {
      maxHealthMultipliers.clear();
      return true;
    }
    else {
      return false; 
    }  
  }

  protected Boolean addGeneralInvulnerability(int DurationTicks) {
   if (allow_applyGeneralInvulnerability.BooleanValue && (DurationTicks > 0)) {
      if (invulnerable.IntegerValue > DurationTicks) {
         invulnerable.IntegerValue = DurationTicks;
      }   
      invulnerable.BooleanValue = true;
      return false;
    }
    else {
      return false;
    }
  }
  protected Boolean clearGeneralInvulnerability() { //-1 Ticks shouldnt be cleared but currently are
   if (allow_clearGeneralInvulnerability.BooleanValue) {
      invulnerable.IntegerValue = 0;
      invulnerable.BooleanValue = true;
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
  if (allow_applyInvulnerability.BooleanValue && (DurationTicks > 0)) {
      if (invulnerabilities[DamageType].IntegerValue > DurationTicks) {
         invulnerabilities[DamageType].IntegerValue = DurationTicks;
      }   
      invulnerabilities[DamageType].BooleanValue = true;
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
  if (allow_clearinvulnerabilities.BooleanValue) {
      invulnerabilities[DamageType].IntegerValue = 0;
      invulnerabilities[DamageType].BooleanValue = true;
      return false;
    }
    else {
      return false;
    }
   }

  protected Boolean addhealthRegenerationReduction(SuperSmashKit by, Double amountPerSecond, int DurationTicks, Boolean preMult) {
    if (allow_applyhealthRegenerationReduction.BooleanValue) {
      if (preMult) {
        healthRegenerationReductionsPreMult.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
      }
      else {
        healthRegenerationReductionsPostMult.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
      }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearhealthRegenerationReductions(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (allow_clearhealthRegenerationReductions.BooleanValue) {
      if (preMult) {
        healthRegenerationReductionsPreMult.clear();
      }
      else {
        healthRegenerationReductionsPostMult.clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addhealthRegenerationIncrease(SuperSmashKit by, Double amountPerSecond, int DurationTicks, Boolean preMult) {
      if (allow_applyhealthRegenerationIncrease.BooleanValue) {
        if (preMult) {
          healthRegenerationIncreasesPreMult.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
        }
        else {
          healthRegenerationIncreasesPostMult.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
        }
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearhealthRegenerationIncreases(Boolean preMult) { //-1 Ticks shouldnt be cleared but currently are
    if (allow_clearhealthRegenerationIncreases.BooleanValue) {
      if (preMult) {
        healthRegenerationIncreasesPreMult.clear();
      }
      else {
        healthRegenerationIncreasesPostMult.clear();
      }
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean addhealthRegenerationMultiplier(SuperSmashKit by, Double amountPerSecond, int DurationTicks) {
    if (allow_applyhealthRegenerationMultiplier.BooleanValue) {
      healthRegenerationMultipliers.add(new IntegerDouble(DurationTicks, (amountPerSecond / 20)));
      return true;
    }
    else {
      return false; 
    }  
  }
  protected Boolean clearhealthRegenerationMultipliers() { //-1 Ticks shouldnt be cleared but currently are
    if (allow_clearhealthRegenerationMultipliers.BooleanValue) {
      healthRegenerationMultipliers.clear();
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
    if (allow_applyResistanceReduction.BooleanValue) {
      if (preMult) {
        resistanceReductionsPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
      }
      else {
        resistanceReductionsPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
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
    if (allow_clearResistanceReductions.BooleanValue) {
      if (preMult) {
        resistanceReductionsPreMult[DamageType].clear();
      }
      else {
        resistanceReductionsPostMult[DamageType].clear();
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
    if (allow_applyResistanceIncrease.BooleanValue) {
        if (preMult) {
          resistanceIncreasesPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          resistanceIncreasesPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
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
    if (allow_clearResistanceIncreases.BooleanValue) {
      if (preMult) {
        resistanceIncreasesPreMult[DamageType].clear();
      }
      else {
        resistanceIncreasesPostMult[DamageType].clear();
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
    if (allow_applyResistanceMultiplier.BooleanValue) {
      resistanceMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return true;
    }
    else {
      return false; 
    }    }
  protected Boolean clearresistanceMultipliers(String DamageType) {
    return clearresistanceMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearresistanceMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
    if (allow_clearresistanceMultipliers.BooleanValue) {
      resistanceMultipliers[DamageType].clear();
      return true;
    }
    else {
      return false; 
    }
  }

  protected Boolean disallowHealing(SuperSmashKit by, int DurationTicks) {
    if (allowHealing.BooleanValue) {
      allowHealing.BooleanValue = false;
      allowHealing.IntegerValue = DurationTicks;
    }
    else {
      if (allowHealing.IntegerValue < DurationTicks) {
        allowHealing.IntegerValue = DurationTicks;
      }
    }
    
    return true;
  }

  protected Boolean disableRegeneration(SuperSmashKit by, int DurationTicks ) {
    if (allowRegeneration.BooleanValue) {
      allowRegeneration.BooleanValue = false;
      allowRegeneration.IntegerValue = DurationTicks;
    }
    else {
      if (allowRegeneration.IntegerValue < DurationTicks) {
        allowRegeneration.IntegerValue = DurationTicks;
      }
    }
    
    return true;
  }
  
  protected Boolean addTakenDamageReduction(SuperSmashKit by, String DamageType, Double amount, int DurationTicks, Boolean preMult) {
    return addTakenDamageReduction(by, stringDamageTypeToInt(DamageType), amount, DurationTicks, preMult);
  }
  protected Boolean addTakenDamageReduction(SuperSmashKit by, int DamageType, Double amount, int DurationTicks, Boolean preMult) {
      if (allow_applyTakenDamageReduction.BooleanValue) {
        if (preMult) {
          takenDamageReductionsPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          takenDamageReductionsPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
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
    if (allow_clearTakenDamageReductions.BooleanValue) {
      if (preMult) {
        takenDamageReductionsPreMult[DamageType].clear();
      }
      else {
        takenDamageReductionsPostMult[DamageType].clear();
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
    if (allow_applyTakenDamageIncrease.BooleanValue) {
      if (preMult) {
        takenDamageIncreasesPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
      }
      else {
        takenDamageIncreasesPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
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
    if (allow_clearTakenDamageIncreases.BooleanValue) {
      if (preMult) {
        takenDamageIncreasesPreMult[DamageType].clear();
      }
      else {
        takenDamageIncreasesPostMult[DamageType].clear();
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
    if (allow_applyTakenDamageMultiplier.BooleanValue) {
      takenDamageMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return true;
    }
    else {
      return false; 
    }    
  }
  protected Boolean clearTakenDamageMultiplier(String DamageType) {
    return cleartakenDamageMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean cleartakenDamageMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
    if (allow_cleartakenDamageMultipliers.BooleanValue) {
      takenDamageMultipliers[DamageType].clear();
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
    if (allow_applyDealedDamageReduction.BooleanValue) {
        if (preMult) {
          inflictedDamageReductionsPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          inflictedDamageReductionsPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
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
      if (allow_clearDealedDamageReductions.BooleanValue) {
      if (preMult) {
        inflictedDamageReductionsPreMult[DamageType].clear();
      }
      else {
        inflictedDamageReductionsPostMult[DamageType].clear();
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
    if (allow_applyDealedDamageIncrease.BooleanValue) {
        if (preMult) {
          inflictedDamageIncreasesPreMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
        }
        else {
          inflictedDamageIncreasesPostMult[DamageType].add(new IntegerDouble(DurationTicks, amount));
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
    if (allow_clearDealedDamageIncreases.BooleanValue) {
      if (preMult) {
        inflictedDamageIncreasesPreMult[DamageType].clear();
      }
      else {
        inflictedDamageIncreasesPostMult[DamageType].clear();
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
    if (allow_applyDealedDamageMultiplier.BooleanValue) {
      inflictedDamageMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return true;
    }
    else {
      return false; 
    }
  }
  protected Boolean clearinflictedDamageMultipliers(String DamageType) {
    return clearinflictedDamageMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearinflictedDamageMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
    if (allow_clearDealedDamageMultipliers.BooleanValue) {
      inflictedDamageMultipliers[DamageType].clear();
      return true;
    }
    else {
      return false; 
    }
  }


  protected Boolean addknockbackTakenMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
    return addknockbackTakenMultiplier(by, stringDamageTypeToInt(DamageType), amount, DurationTicks);
  }
  protected Boolean addknockbackTakenMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
    if (allow_applyknockbackTakenMultiplier.BooleanValue) {
      knockbackTakenMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return false;
    }
    else {
      return false;
    }
  }
  protected Boolean clearknockbackTakenMultipliers(String DamageType) {
    return clearknockbackTakenMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearknockbackTakenMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
    if (allow_clearknockbackTakenMultipliers.BooleanValue) {
      knockbackTakenMultipliers[DamageType].clear();
      return false;
    }
    else {
      return false;
    }
  }

  protected Boolean addknockbackTakenDirectionalMultiplier(SuperSmashKit by, String DamageType, Vector multiplier, int DurationTicks) {
    return addknockbackTakenDirectionalMultiplier(by, stringDamageTypeToInt(DamageType), multiplier, DurationTicks);
  }
  protected Boolean addknockbackTakenDirectionalMultiplier(SuperSmashKit by, int DamageType, Vector multiplier, int DurationTicks) {
    if (allow_applyknockbackTakenDirectionalMultiplier.BooleanValue) {
      knockbackDealedDirectionalMultipliers[DamageType].add(new IntegerVector(DurationTicks, multiplier.clone()));
      return false;
    }
    else {
      return false;
    }
  }
  protected Boolean clearknockbackTakenDirectionalMultipliers(String DamageType) {
    return clearknockbackTakenDirectionalMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearknockbackTakenDirectionalMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
   if (allow_clearknockbackTakenDirectionalMultipliers.BooleanValue) {
      knockbackTakenDirectionalMultipliers[DamageType].clear();
      return false;
    }
    else {
      return false;
    }
   }

  protected Boolean addknockbackDealedMultiplier(SuperSmashKit by, String DamageType, Double amount, int DurationTicks) {
    return addknockbackDealedMultiplier(by, stringDamageTypeToInt(DamageType), amount, DurationTicks);
  }
  protected Boolean addknockbackDealedMultiplier(SuperSmashKit by, int DamageType, Double amount, int DurationTicks) {
    if (allow_applyknockbackDealedMultiplier.BooleanValue) {
      knockbackDealedMultipliers[DamageType].add(new IntegerDouble(DurationTicks, amount));
      return false;
    }
    else {
      return false;
    }
  }
  protected Boolean clearknockbackDealedMultipliers(String DamageType) {
    return clearknockbackDealedMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearknockbackDealedMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
   if (allow_clearknockbackDealedMultipliers.BooleanValue) {
      knockbackDealedDirectionalMultipliers[DamageType].clear();
      return false;
    }
    else {
      return false;
    }
  }

  protected Boolean addknockbackDealedDirectionalMultiplier(SuperSmashKit by, String DamageType, Vector multiplier, int DurationTicks) {
    return addknockbackDealedDirectionalMultiplier(by, stringDamageTypeToInt(DamageType), multiplier, DurationTicks);
  }
  protected Boolean addknockbackDealedDirectionalMultiplier(SuperSmashKit by, int DamageType, Vector multiplier, int DurationTicks) {
   if (allow_applyknockbackDealedDirectionalMultiplier.BooleanValue) {
      knockbackDealedDirectionalMultipliers[DamageType].add(new IntegerVector(DurationTicks, multiplier.clone()));
      return false;
    }
    else {
      return false;
    }
   }
  protected Boolean clearknockbackDealedDirectionalMultipliers(String DamageType) {
    return clearknockbackDealedDirectionalMultipliers(stringDamageTypeToInt(DamageType));
  }
  protected Boolean clearknockbackDealedDirectionalMultipliers(int DamageType) { //-1 Ticks shouldnt be cleared but currently are
   if (allow_clearknockbackDealedDirectionalMultipliers.BooleanValue) {
      knockbackDealedDirectionalMultipliers[DamageType].clear();
      return false;
    }
    else {
      return false;
    }
   }


  protected Boolean effectRemove(String effectName) {
    return false;  
  }
  protected Boolean effectRemove(String effectName, SuperSmashKit by) { 
    return false;
  }
  protected Boolean effectExists(String effectName) {
    return false;
  }
  protected Boolean effectExists(String effectName, SuperSmashKit by) { 
    return false;
  }
  protected EffectOverTime[] effectGet(String effectName) {
    return null;
  }
  protected EffectOverTime[] effectGet(String effectName, SuperSmashKit by) {
    return null;
  }
  protected Boolean effectApply(String effectName, SuperSmashKit by) {
    return false;
  }


  public String[] createBaseDescription() { //Creates a description, based on the const values, abilitys can be added by the specific kit
    String[] toReturn = new String[6];
  
    toReturn[0] = "health: " + Double.toString(maxHealth);
    toReturn[1] = "Regeneration per sec: " + Double.toString(Math.round(healthRegenerationPerTick * 20 * 10)/10.0);
    toReturn[2] = "Damage: " + Double.toString(attackDamage);
    toReturn[3] = "Armor: " + Double.toString(resistances[1]);
    toReturn[4] = "knockbackDealed: " + Double.toString(knockbackDealed);
    toReturn[5] = "knockbackTaken: " + Double.toString(knockbackTaken);
    
    getPlayer().sendMessage(toReturn[0]);
    
    return toReturn;
  }
  // /* For a help unit
  public List<Vector> calculateLinePositions(Vector startingPosition, Vector facingDirection, Double length, int count, Boolean random) {
    List<Vector> toReturn = new ArrayList(0);
    Vector vec = facingDirection.clone().normalize().multiply(length);
    Vector curPos = startingPosition.clone();
    
    if (count > 0) {
      if (random) {
        
      }
      else {
        //not random so a smooth Line
        toReturn.add(curPos.clone());
        
        for ( int i = 1 ; i < count ; i++ ) {
          curPos.add(vec);
          toReturn.add(curPos.clone());
        }
        
      }
    }
    
    return toReturn;
  }
  public List<Vector> calculateRectanglePositions(Vector facingDirection, Double horizontal, Double vertical, int count, Boolean surfaceOnly, Boolean random) {
    List<Vector> toReturn = new ArrayList(0);
    
    return toReturn;
  }
  public List<Vector> calculateCirclePositions(Vector facingDirection, Double radius, int count, Boolean surfaceOnly, Boolean random) {
    List<Vector> toReturn = new ArrayList(0);
    
    return toReturn;
  }
  public List<Vector> calculateCuboidPositions(Vector facingDirection, Double horizontalAway, Double horizontal2, Double vertical, int count, Boolean surfaceOnly, Boolean random) {
    List<Vector> toReturn = new ArrayList(0);
    
    return toReturn;
  }
  //hardcoremode
  public List<Vector> calculateSpherePositions(Double radius, int count, Boolean surfaceOnly, Boolean random) {
    List<Vector> toReturn = new ArrayList(0);
    
    return toReturn;
  }
  // */
}
