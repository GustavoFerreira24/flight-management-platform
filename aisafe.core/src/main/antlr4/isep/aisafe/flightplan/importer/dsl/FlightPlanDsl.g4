grammar FlightPlanDsl;

flightPlanFile
    : flightBlock EOF
    ;

flightBlock
    : FLIGHT flightIdentifier flightType LBRACE flightMetadata* legBlock+ RBRACE
    ;

flightIdentifier
    : FLIGHT_DESIGNATOR
    | IDENTIFIER
    ;

flightType
    : REGULAR
    | CHARTER
    ;

flightMetadata
    : aircraftStatement
    | pilotStatement
    ;

aircraftStatement
    : AIRCRAFT aircraftRegistration SEMI
    ;

pilotStatement
    : PILOT pilotIdentifier SEMI
    ;

aircraftRegistration
    : IDENTIFIER
    ;

pilotIdentifier
    : IDENTIFIER
    ;

legBlock
    : LEG LBRACE departureStatement arrivalStatement routeStatement segmentStatement+ fuelStatement RBRACE
    ;

departureStatement
    : DEPARTURE airportCode AT dateTime SEMI
    ;

arrivalStatement
    : ARRIVAL airportCode AT dateTime SEMI
    ;

routeStatement
    : ROUTE airportCode ARROW airportCode SEMI
    ;

segmentStatement
    : SEGMENT coordinate ARROW coordinate altitudeClause? widthClause? windClause? SEMI
    ;

altitudeClause
    : ALTITUDE LBRACK number COMMA number RBRACK unit?
    ;

widthClause
    : WIDTH number unit?
    ;

windClause
    : WIND number DEG? number windUnit?
    ;

fuelStatement
    : FUEL number unit SEMI
    ;

coordinate
    : LPAREN number COMMA number RPAREN
    ;

airportCode
    : AIRPORT_CODE
    ;

dateTime
    : DATE_TIME
    | DATE TIME
    ;

number
    : NUMBER
    ;

unit
    : UNIT
    ;

windUnit
    : WIND_UNIT
    | UNIT
    ;

FLIGHT      : F L I G H T;
LEG         : L E G;
DEPARTURE   : D E P A R T U R E;
ARRIVAL     : A R R I V A L;
ROUTE       : R O U T E;
SEGMENT     : S E G M E N T;
FUEL        : F U E L;
AIRCRAFT    : A I R C R A F T;
PILOT       : P I L O T;
AT          : A T;
REGULAR     : R E G U L A R;
CHARTER     : C H A R T E R;
ALTITUDE    : A L T I T U D E;
WIDTH       : W I D T H;
WIND        : W I N D;
DEG         : D E G;

DATE_TIME   : DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT 'T' DIGIT DIGIT ':' DIGIT DIGIT;
DATE        : DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT;
TIME        : DIGIT DIGIT ':' DIGIT DIGIT;
FLIGHT_DESIGNATOR
            : UPPER UPPER DIGIT DIGIT? DIGIT? DIGIT? UPPER?
            ;
AIRPORT_CODE
            : UPPER UPPER UPPER UPPER?
            ;
NUMBER      : '-'? DIGIT+ ('.' DIGIT+)?;
WIND_UNIT   : M '/' S;
UNIT        : K G | LITRE | M | F T | K M | N M;
IDENTIFIER  : [A-Za-z_][A-Za-z0-9_.-]*;

ARROW       : '->';
LBRACE      : '{';
RBRACE      : '}';
LPAREN      : '(';
RPAREN      : ')';
LBRACK      : '[';
RBRACK      : ']';
COMMA       : ',';
SEMI        : ';';
COLON       : ':';

WS          : [ \t\r\n]+ -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;

fragment DIGIT : [0-9];
fragment UPPER : [A-Z];
fragment A : [aA];
fragment B : [bB];
fragment C : [cC];
fragment D : [dD];
fragment E : [eE];
fragment F : [fF];
fragment G : [gG];
fragment H : [hH];
fragment I : [iI];
fragment J : [jJ];
fragment K : [kK];
fragment L : [lL];
fragment M : [mM];
fragment N : [nN];
fragment O : [oO];
fragment P : [pP];
fragment Q : [qQ];
fragment R : [rR];
fragment S : [sS];
fragment T : [tT];
fragment U : [uU];
fragment V : [vV];
fragment W : [wW];
fragment X : [xX];
fragment Y : [yY];
fragment Z : [zZ];
fragment LITRE : [lL];
