# Lobotomy Corporation Assembly - Example 1: Basic Energy Extraction
# Demonstrates: winst, winsight, wattach, extract, add

.text
main:
    #### Initialize registers (clear to 0) ####
    sub $t0,$t0,$t0   # agent Fortitude / HP
    sub $t1,$t1,$t1   # abnormality base level
    sub $t2,$t2,$t2   # extracted PE result
    sub $s0,$s0,$s0   # facility energy quota

    #### Agent stat training ####
    # Boost agent HP using Instinct work
    winst $t0,$t0,15      # +15 Fortitude
    winst $t0,$t0,15      # +15 again (total +30 from 0)

    #### Abnormality work to build base PE ####
    # Use Insight and Attachment to simulate a non-zero base
    winsight $t1,$t1,10   # +10 Prudence / energy potential
    wattach  $t1,$t1,8    # +8 Temperance / success rate

    #### First extraction ####
    # EXACT FORM: extract $t1,$t2,1
    # Here: rd = $t2, rs = $t1, risk imm = 1 (TETH)
    extract $t2,$t1,1     # $t2 = base($t1) + scaled random (TETH)

    # Add extracted PE to global quota in $s0
    add $s0,$s0,$t2

    #### Second extraction (repeated work) ####
    extract $t2,$t1,1     # another extraction with same abnormality
    add $s0,$s0,$t2       # update quota again

end:
    j end                 # idle loop
