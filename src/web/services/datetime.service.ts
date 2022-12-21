import { Injectable } from '@angular/core';
import moment from 'moment-timezone';
import { DateFormat, TimeFormat } from '../types/datetime-const';
import { TimezoneService } from './timezone.service';

@Injectable({
    providedIn: 'root',
})
export class DateTimeService {
    constructor(private timezoneService: TimezoneService) { }

    /**
     * Get the local date and time of timezone from timestamp.
     */
    getDateTimeAtTimezone(
        timestamp: number,
        timeZone: string,
        resolveMidnightTo2359: boolean,
    ): { date: DateFormat, time: TimeFormat } {
        let momentInstance: moment.Moment = this.timezoneService.getMomentInstance(
            timestamp,
            timeZone,
        );
        if (
            resolveMidnightTo2359
            && momentInstance.hour() === 0
            && momentInstance.minute() === 0
        ) {
            momentInstance = momentInstance.subtract(1, 'minute');
        }
        const date: DateFormat = {
            year: momentInstance.year(),
            month: momentInstance.month() + 1, // moment return 0-11 for month
            day: momentInstance.date(),
        };
        const time: TimeFormat = {
            minute: momentInstance.minute(),
            hour: momentInstance.hour(),
        };
        return {
            date,
            time,
        };
    }

    /**
     * Converts a Date object to a TimeFormat object.
     *
     * @param date Date to be converted into TimeFormat object.
     */
    convertDateToTimeFormat(date: Date): TimeFormat {
        return { hour: date.getHours(), minute: date.getMinutes() };
    }
}
