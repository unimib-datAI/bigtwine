export enum OptionType {
    Choices = 'choices',
    Text = 'text',
    Number = 'number',
    Boolean = 'boolean',
}

export class ChoiceEntry {
    constructor(readonly value, readonly label) {}
}

export interface IOptionConfig {
    readonly optionType: OptionType;
    key: string;
    defaultValue: any;
    label: string;
    helpText: string;
}

export interface IOptionChange {
    optionKey: string;
    value: any;
}

export class ChoicesOptionConfig implements IOptionConfig {
    get optionType(): OptionType {
        return OptionType.Choices;
    }

    constructor(
        readonly key: string,
        readonly defaultValue: any,
        readonly label: string,
        readonly helpText: string,
        readonly choices: ChoiceEntry[]) {}

}

export class TextOptionConfig implements IOptionConfig {
    get optionType(): OptionType {
        return OptionType.Text;
    }

    constructor(
        readonly key: string,
        readonly defaultValue: any,
        readonly label: string,
        readonly helpText: string) {}
}

export class NumberOptionConfig implements IOptionConfig {
    get optionType(): OptionType {
        return OptionType.Number;
    }

    constructor(
        readonly key: string,
        readonly defaultValue: number,
        readonly label: string,
        readonly helpText: string,
        readonly minValue: number,
        readonly maxValue: number,
        readonly step: number) {
    }
}

export class BooleanOptionConfig implements IOptionConfig {
    get optionType(): OptionType {
        return OptionType.Boolean;
    }

    constructor(
        readonly key: string,
        readonly defaultValue: boolean,
        readonly label: string,
        readonly helpText: string) {}
}

export interface IOptionsGroup {
    label: string;
    options: IOptionConfig[];
    valueGetter(key: string): any;
    changeHandler(key: string, value: any);
}
