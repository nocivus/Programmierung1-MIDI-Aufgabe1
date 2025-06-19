public class MIDItools {
    //Aufgabe 1
    public static byte getNote(char note, int octave, boolean sharp){

        int Note;
        if (note == 'C'){
            Note = 0;
        }
        else if (note == 'D'){
            Note = 2;
        }
        else if (note == 'E'){
            Note = 4;
        }
        else if (note == 'F'){
            Note = 5;
        }
        else if (note == 'G'){
            Note = 7;
        }
        else if (note == 'A'){
            Note = 9;
        }
        else if (note == 'B'){
            Note = 11;
        }
        else {
            Note = 128;
        }

        int Sharp;
        if(sharp == true){
            Sharp = 1;
        }
        else {
            Sharp = 0;
        }

        int midiNote = (octave+1)*12 + Note + Sharp;


        byte MidiNote = (byte) midiNote;
        if(midiNote<=127) {
            return MidiNote;
        }
        else {
            return 0;
        }
    }




    //Aufgabe 2
    public static byte[] getHeader(byte speed){

        byte[] header = new byte[] {0x4D, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06, 0x00, 0x00, 0x00, 0x01, 0x00, speed};

        return header;
    }



    //Aufgabe 3
    public static byte[] getNoteEvent(byte delay, boolean noteOn, byte note, byte velocity){

        byte state;
        if(noteOn==true){
            state=(byte) 0b10010000;
        }
        else {
            state=(byte) 0b10000000;
        }
        return new byte[] {delay, state, note, velocity};
    }



    //Aufgabe 4
    public static byte[] addNoteToTrack(byte[] trackdata, byte[] noteEvent){

        int length = trackdata.length+noteEvent.length;
        byte[] newTrack = new byte[length];
        for (int i=0; i<trackdata.length; i++){
            newTrack[i] = trackdata[i];
        }
        for (int i=0; i<noteEvent.length; i++){
            newTrack[trackdata.length+i] = noteEvent[i];
        }
        return newTrack;
    }



    //Aufgabe 5
    public static byte[] getTrack(byte instrument, byte[] trackdata){

        int length = trackdata.length + 18;

        byte[] header = new byte[] {0x4D, 0x54, 0x72, 0x6B, 0x00, 0x00, 0x00};                                                          //7
        byte[] trackLength = new byte[] {(byte)length};                                                                                 //1
        byte[] speed = new byte[] {0x00, (byte) 0xFF, 0x58, 0x04, 0x04, 0x02, 0x18, 0x08, 0x00, (byte) 0xFF, 0x51, 0x03, 0x07, (byte) 0xA1, 0x20};            //15
        byte[] channel = new byte[] {0x00, (byte) 0xC0};                                                                                      //2
        byte[] Instrument = new byte[] {instrument};
        byte[] end = new byte[] {(byte) 0xFF, 0x2F, 0x00};

        byte[] track = addNoteToTrack(header, trackLength);
        track = addNoteToTrack(track, speed);
        track = addNoteToTrack(track, channel);
        track = addNoteToTrack(track, Instrument);
        track = addNoteToTrack(track, trackdata);
        track = addNoteToTrack(track, end);

        return track;
    }


    public static void main(String[] args) {
        System.out.println(getNote('A', 4, true));
        System.out.println(getTrack((byte)0x05, new byte[] {(byte)0x30, (byte)0b10010000, (byte)12, (byte)120}));
    }
}
