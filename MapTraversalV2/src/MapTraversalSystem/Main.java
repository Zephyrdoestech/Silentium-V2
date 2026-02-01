package MapTraversalSystem;

import java.util.Scanner;

public class Main{

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        int choice;
        boolean exploring;

        //Object Instantiate
        TownOfEchoes townOfEchoes = new TownOfEchoes();
        SilentCaverns silentCaverns = new SilentCaverns();
        AbyssOfDissonance abyssOfDissonance = new AbyssOfDissonance();
        MapCharacter mapCharacter = new MapCharacter();

        LyronMapMonologue lyronMono = new LyronMapMonologue();
        AureliusMapMonologue aureliusMono = new AureliusMapMonologue();
        SonaraMapMonologue sonaraMono = new SonaraMapMonologue();


        //Setting The Maps
        townOfEchoes.setMap();
        silentCaverns.setMap();
        abyssOfDissonance.setMap();

        mapCharacter.explore(townOfEchoes,lyronMono);
        mapCharacter.explore(silentCaverns,aureliusMono);
        mapCharacter.explore(abyssOfDissonance,sonaraMono);
    }
}
