# Lobotomy Corporation Assembly - Example 3: Ordeal and Meltdown
# Demonstrates: winst, ordeal, meltdown

.text
main:
    #### Clear and set up department register ####
    sub $s1,$s1,$s1       # department id container
    winst $s1,$s1,15      # give it some non-zero value (e.g., "department strength")

    #### Trigger Ordeal events ####
    ordeal 0              # Dawn ordeal hits a random $t register

    # Trigger another ordeal (still immediate 0 form)
    ordeal 1

    #### Trigger full department meltdown ####
    # EXACT FORM: meltdown $s1
    meltdown $s1          # Penalizes all $t registers, increases global risk

end:
    j end                 # idle loop
